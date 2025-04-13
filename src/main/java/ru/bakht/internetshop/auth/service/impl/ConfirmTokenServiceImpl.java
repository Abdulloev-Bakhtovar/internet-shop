package ru.bakht.internetshop.auth.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bakht.internetshop.exception.AppException;
import ru.bakht.internetshop.auth.model.Token;
import ru.bakht.internetshop.auth.model.User;
import ru.bakht.internetshop.auth.model.dto.ResetPasswordDto;
import ru.bakht.internetshop.auth.model.enums.ChangeType;
import ru.bakht.internetshop.auth.model.enums.EmailTemplateName;
import ru.bakht.internetshop.auth.model.enums.TokenTypeName;
import ru.bakht.internetshop.auth.repository.TokenRepo;
import ru.bakht.internetshop.auth.repository.UserRepo;
import ru.bakht.internetshop.auth.service.*;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class ConfirmTokenServiceImpl implements ConfirmTokenService {

    private final TokenRepo tokenRepo;
    private final UserRepo userRepo;
    private final NotificationService notificationService;
    private final PasswordEncoder passwordEncoder;
    private final TokenTypeService tokenTypeService;
    private final LoginInfoChangeService loginInfoChangeService;
    private final AuthService authService;

    @Override
    @Transactional(noRollbackFor = AppException.class)
    public void confirmTokenFromEmail(String token,
                                      TokenTypeName tokenTypeName,
                                      HttpServletRequest request,
                                      HttpServletResponse response
    ) {
        Token savedToken = getToken(token, tokenTypeName);
        validateTokenExpiration(savedToken);

        processValidToken(savedToken, request, response);
        tokenRepo.delete(savedToken);
    }

    @Override
    @Transactional
    public void confirmTokenFromEmailAndResetPass(ResetPasswordDto resetPasswordDto,
                                                  String token,
                                                  TokenTypeName tokenTypeName
    ) {
        Token savedToken = getToken(token, tokenTypeName);
        validateTokenExpiration(savedToken);

        var user = savedToken.getUser();
        user.setPassword(passwordEncoder.encode(resetPasswordDto.getNewPassword()));
        userRepo.save(user);
        tokenRepo.delete(savedToken);
    }

    private Token getToken(String token, TokenTypeName tokenTypeName) {
        var tokenType = tokenTypeService.getByName(tokenTypeName);

        return tokenRepo.findByTokenAndTokenType(token, tokenType)
                .orElseThrow(() -> new AppException(
                        "Token not found",
                        HttpStatus.NOT_FOUND)
                );
    }

    private void validateTokenExpiration(Token token) {
        if (Instant.now().isAfter(token.getExpiresDate())) {
            tokenRepo.delete(token);
            notificationService.sendEmailForUserAction(
                    token.getUser().getEmail(),
                    determineEmailTemplateName(token.getTokenType().getName()),
                    token.getUser()
            );
            throw new AppException("Token expired. New token was sent to the email: " + token.getUser().getEmail(),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    private void processValidToken(Token savedToken, HttpServletRequest request, HttpServletResponse response) {
        switch (savedToken.getTokenType().getName()) {
            case ACTIVATION -> activationAccount(savedToken.getUser());
            case EMAIL_CHANGE -> emailChange(savedToken.getUser(), request, response);
            case PASSWORD_CHANGE -> passwordChange(savedToken.getUser(), request, response);
            default -> throw new AppException(
                    "Unknown token type: " + savedToken.getTokenType().getName(),
                    HttpStatus.NOT_FOUND
            );
        }
    }

    private void activationAccount(User user) {
        user.setEmailVerified(true);
        userRepo.save(user);
    }

    private void emailChange(User user, HttpServletRequest request, HttpServletResponse response) {
        var loginInfoChange = loginInfoChangeService.getByUserAndChangeType(user, ChangeType.EMAIL);
        user.setEmail(loginInfoChange.getValue());

        userRepo.save(user);
        authService.logout(request, response);
    }

    private void passwordChange(User user, HttpServletRequest request, HttpServletResponse response) {
        var loginInfoChange = loginInfoChangeService.getByUserAndChangeType(user, ChangeType.PASSWORD);
        user.setPassword(passwordEncoder.encode(loginInfoChange.getValue()));

        userRepo.save(user);
        authService.logout(request, response);
    }

    private EmailTemplateName determineEmailTemplateName(TokenTypeName tokenTypeName) {
        return switch (tokenTypeName) {
            case ACTIVATION -> EmailTemplateName.ACTIVATE_ACCOUNT;
            case EMAIL_CHANGE -> EmailTemplateName.CHANGE_EMAIL;
            case PASSWORD_RESET -> EmailTemplateName.RESET_PASSWORD;
            case PASSWORD_CHANGE -> EmailTemplateName.CHANGE_PASSWORD;
        };
    }
}