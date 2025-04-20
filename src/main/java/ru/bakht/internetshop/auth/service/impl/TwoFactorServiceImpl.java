package ru.bakht.internetshop.auth.service.impl;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bakht.internetshop.exception.AppException;
import ru.bakht.internetshop.auth.mapper.TokenMapper;
import ru.bakht.internetshop.auth.model.dto.TokenDto;
import ru.bakht.internetshop.auth.model.enums.EmailTemplateName;
import ru.bakht.internetshop.auth.repository.UserRepo;
import ru.bakht.internetshop.auth.service.*;
import ru.bakht.internetshop.auth.util.AuthUtils;
import ru.bakht.internetshop.auth.util.CookieUtil;
import ru.bakht.internetshop.auth.util.TokenGeneratorUtil;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class TwoFactorServiceImpl implements TwoFactorService {

    private static final String PREFIX = "2fa:";
    private static final long CODE_TTL_MINUTES = 5;
    private static final int tokenLength = 6;

    private final RedisTemplate<String, String> redisTemplate;
    private final EmailService emailService;
    private final JwtService jwtService;
    private final CookieUtil cookieUtil;
    private final LoginAttemptService loginAttemptService;
    private final TokenMapper tokenMapper;
    private final UserService userService;
    private final AuthUtils authUtils;
    private final UserRepo userRepo;

    @Override
    public void generateAndSendCode(String email) {
        String code = TokenGeneratorUtil.generateNumericCode(tokenLength);
        String redisKey = PREFIX + email;

        redisTemplate.opsForValue().set(
                redisKey,
                code,
                CODE_TTL_MINUTES,
                TimeUnit.MINUTES
        );

        try {
            emailService.sendEmail(
                    email,
                    EmailTemplateName.TWO_FACTOR,
                    code,
                    "Код подтверждения входа (2FA)",
                    null);
        } catch (MessagingException e) {
            throw new AppException("Error send 2fa code", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public TokenDto verifyCode(String email, String code, HttpServletResponse response) {
        String redisKey = PREFIX + email;
        String storedCode = redisTemplate.opsForValue().get(redisKey);

        if (storedCode == null) {
            throw new AppException("Code not found", HttpStatus.NOT_FOUND);
        }

        boolean isValid = code.equals(storedCode);
        if (isValid) {
            redisTemplate.delete(redisKey);

            var user = userService.getByEmail(email);
            var accessToken = jwtService.generateAccessToken(user);
            var refreshToken = jwtService.generateRefreshToken(user);

            cookieUtil.addRefreshTokenToCookie(response, refreshToken);
            loginAttemptService.loginSuccess(email);

            return tokenMapper.toDto(accessToken);
        }
        throw new AppException("Code verification failed", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public void changeTwoFactor(boolean isTwoFactorEnabled) {
        var user = authUtils.getAuthenticatedUser();

        user = userService.getById(user.getId());
        user.setTwoFactorEnabled(isTwoFactorEnabled);

        userRepo.save(user);
    }
}

