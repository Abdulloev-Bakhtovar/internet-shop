package ru.bakht.internetshop.auth.service.impl;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bakht.internetshop.exception.AppException;
import ru.bakht.internetshop.auth.model.User;
import ru.bakht.internetshop.auth.model.enums.EmailTemplateName;
import ru.bakht.internetshop.auth.model.enums.TokenTypeName;
import ru.bakht.internetshop.auth.service.EmailService;
import ru.bakht.internetshop.auth.service.NotificationService;
import ru.bakht.internetshop.auth.service.TokenService;
import ru.bakht.internetshop.auth.util.TokenGeneratorUtil;


@Service
@Transactional
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    @Value("${application.security.jwt.confirm-token.length}")
    private int confirmTokenLength;

    private final EmailService emailService;
    private final TokenService tokenService;

    @Override
    public void sendEmailForUserAction(String messageRecipient, EmailTemplateName templateType, User user) {

        String subject = getEmailSubjectByTemplate(templateType);
        String confirmToken = TokenGeneratorUtil.generateRandomString(confirmTokenLength);
        tokenService.save(user, confirmToken, determineTokenType(templateType));

        try {
            emailService.sendEmail(
                    messageRecipient,
                    templateType,
                    confirmToken,
                    subject
            );
        } catch (MessagingException e) {
            throw new AppException(
                    "Failed to send email to address: " + messageRecipient, HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    private String getEmailSubjectByTemplate(EmailTemplateName templateType) {

        return switch (templateType) {
            case ACTIVATE_ACCOUNT -> "Активация учетной записи";
            case CHANGE_EMAIL -> "Подтверждение изменения email";
            case RESET_PASSWORD -> "Подтверждение сброса пароля";
            case CHANGE_PASSWORD -> "Подтверждение изменения пароля";
            default -> throw new AppException("Unexpected value: " + templateType, HttpStatus.INTERNAL_SERVER_ERROR);
        };
    }

    private TokenTypeName determineTokenType(EmailTemplateName templateName) {

        return switch (templateName) {
            case ACTIVATE_ACCOUNT -> TokenTypeName.ACTIVATION;
            case CHANGE_EMAIL -> TokenTypeName.EMAIL_CHANGE;
            case RESET_PASSWORD -> TokenTypeName.PASSWORD_RESET;
            case CHANGE_PASSWORD -> TokenTypeName.PASSWORD_CHANGE;
            default -> throw new AppException("Unexpected value: " + templateName, HttpStatus.INTERNAL_SERVER_ERROR);
        };
    }
}
