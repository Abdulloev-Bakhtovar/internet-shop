package ru.bakht.internetshop.auth.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bakht.internetshop.auth.exception.KvadroksException;
import ru.bakht.internetshop.auth.mapper.TokenMapper;
import ru.bakht.internetshop.auth.mapper.UserMapper;
import ru.bakht.internetshop.auth.model.User;
import ru.bakht.internetshop.auth.model.dto.EmailAndPassDto;
import ru.bakht.internetshop.auth.model.dto.TokenDto;
import ru.bakht.internetshop.auth.model.enums.EmailTemplateName;
import ru.bakht.internetshop.auth.model.enums.RoleName;
import ru.bakht.internetshop.auth.repository.UserRepo;
import ru.bakht.internetshop.auth.service.*;
import ru.bakht.internetshop.auth.util.AuthUtils;
import ru.bakht.internetshop.auth.util.CookieUtil;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final CookieUtil cookieUtil;
    private final AuthUtils authUtils;
    private final RoleService roleService;
    private final UserService userService;
    private final JwtService jwtService;
    private final UserRepo userRepository;
    private final NotificationService notificationService;
    private final TokenBlacklistService tokenBlacklistService;
    private final LoginInfoChangeService loginInfoChangeService;
    private final LoginAttemptService loginAttemptService;
    private final UserMapper userMapper;
    private final TokenMapper tokenMapper;
    private final CaptchaValidatorService captchaValidatorService;
    private final TwoFactorService twoFactorService;

    @Override
    @Transactional
    public void register(EmailAndPassDto registerDto, HttpServletRequest request) {

        verifyCaptchaIfRequired(registerDto, request);
        validateUserDoesNotExist(registerDto.getEmail());

        var userRole = roleService.getByName(RoleName.USER);
        var user = userMapper.toEntity(registerDto, userRole);

        userRepository.save(user);

        notificationService.sendEmailForUserAction(user.getEmail(), EmailTemplateName.ACTIVATE_ACCOUNT, user);
    }

    private void validateUserDoesNotExist(String email) {

        userService.validateUserDoesNotExist(email);
        loginInfoChangeService.validateUserDoesNotExist(email);
    }

    @Override
    @Transactional(readOnly = true)
    public TokenDto login(EmailAndPassDto loginDto, HttpServletRequest request, HttpServletResponse response) {

        checkAccountLocked(loginDto.getEmail());
        verifyCaptchaIfRequired(loginDto, request);

        var user = userService.getByEmail(loginDto.getEmail());
        validateUserForLogin(user);
        AuthUtils.validateCredentials(loginDto.getEmail(), loginDto.getPassword());

        // Проверяем, требуется ли 2FA
        if (user.isTwoFactorEnabled()) {
            // Генерация и отправка 2FA кода на email
            twoFactorService.generateAndSendCode(user.getEmail());

            // Возвращаем ответ с просьбой ввести 2FA код
            throw  new KvadroksException("Введите код 2FA, отправленный на вашу почту", HttpStatus.OK);
        }

        // Если 2FA не требуется, генерируем токены
        var accessToken = jwtService.generateAccessToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);

        cookieUtil.addRefreshTokenToCookie(response, refreshToken);
        loginAttemptService.loginSuccess(loginDto.getEmail());

        return tokenMapper.toDto(accessToken);
    }

    @Override
    public TokenDto refreshToken(HttpServletRequest request, HttpServletResponse response) {

        String refreshToken = cookieUtil.extractRefreshTokenFromCookies(request);

        User user = validateRefreshTokenAndGetUser(refreshToken, response);

        String newAccessToken = jwtService.generateAccessToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);

        cookieUtil.addRefreshTokenToCookie(response, newRefreshToken);

        return tokenMapper.toDto(newAccessToken);
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response) {

        String refreshToken = cookieUtil.extractRefreshTokenFromCookies(request);
        if (refreshToken != null && jwtService.isTokenSignatureValid(refreshToken)
                && !jwtService.isTokenExpired(refreshToken)) {
            tokenBlacklistService.addToBlacklist(refreshToken);
        }

        Optional.ofNullable(request.getHeader("Authorization"))
                .filter(header -> header.startsWith("Bearer "))
                .map(header -> header.substring(7))
                .filter(accessToken -> !jwtService.isTokenExpired(accessToken))
                .ifPresent(tokenBlacklistService::addToBlacklist);

        cookieUtil.deleteRefreshTokenFromCookies(response);
    }

    @Override
    @Transactional
    public void delete(HttpServletResponse response) {

        var user = authUtils.getAuthenticatedUser();
        user = userService.getById(user.getId());

        cookieUtil.deleteRefreshTokenFromCookies(response);
        userRepository.delete(user);
    }

    private void validateUserForLogin(User user) {
        if (user.isAccountLocked() || !user.isEnabled()) {
            throw new KvadroksException(
                    "Account with email " + user.getEmail() + " is blocked or not verified",
                    HttpStatus.FORBIDDEN
            );
        }
    }

    private void checkAccountLocked(String email) {
        if (loginAttemptService.isAccountLocked(email)) {
            throw new KvadroksException(
                    "Account locked until: " + loginAttemptService.getAccountUnlockTime(email),
                    HttpStatus.TOO_MANY_REQUESTS
            );
        }
    }

    private void verifyCaptchaIfRequired(EmailAndPassDto loginDto, HttpServletRequest request) {
        String clientIp = AuthUtils.getClientIp(request);

        if (loginAttemptService.isCaptchaRequired(loginDto.getEmail()) &&
                !captchaValidatorService.validate(loginDto.getCaptchaToken(), clientIp)) {
            loginAttemptService.loginFailed(loginDto.getEmail());
            throw new KvadroksException("CAPTCHA verification failed", HttpStatus.UNAUTHORIZED);
        }
    }

    private User validateRefreshTokenAndGetUser(String refreshToken, HttpServletResponse response) {

        if (refreshToken == null || refreshToken.isBlank()) {
            cookieUtil.deleteRefreshTokenFromCookies(response);
            throw new KvadroksException("Refresh token not found in cookies", HttpStatus.UNAUTHORIZED);
        }

        if (tokenBlacklistService.isBlacklisted(refreshToken)) {
            cookieUtil.deleteRefreshTokenFromCookies(response);
            throw new KvadroksException("Token blacklisted", HttpStatus.UNAUTHORIZED);
        }

        if (!jwtService.isTokenSignatureValid(refreshToken)) {
            cookieUtil.deleteRefreshTokenFromCookies(response);
            throw new KvadroksException("Invalid token signature", HttpStatus.UNAUTHORIZED);
        }

        if (!jwtService.isRefreshToken(refreshToken)) {
            cookieUtil.deleteRefreshTokenFromCookies(response);
            throw new KvadroksException("Invalid token type", HttpStatus.FORBIDDEN);
        }

        if (jwtService.isTokenExpired(refreshToken)) {
            cookieUtil.deleteRefreshTokenFromCookies(response);
            throw new KvadroksException("Token expired", HttpStatus.UNAUTHORIZED);
        }

        String userId = jwtService.extractUserId(refreshToken);
        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> {
                    cookieUtil.deleteRefreshTokenFromCookies(response);
                    return new KvadroksException("User not found", HttpStatus.NOT_FOUND);
                });

        if (user.isAccountLocked() || !user.isEnabled()) {
            cookieUtil.deleteRefreshTokenFromCookies(response);
            throw new KvadroksException("Account is blocked or not verified", HttpStatus.FORBIDDEN);
        }

        return user;
    }
}
