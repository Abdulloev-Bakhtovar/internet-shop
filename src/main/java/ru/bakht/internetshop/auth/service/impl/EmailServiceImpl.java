package ru.bakht.internetshop.auth.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import ru.bakht.internetshop.auth.exception.KvadroksException;
import ru.bakht.internetshop.auth.model.enums.EmailTemplateName;
import ru.bakht.internetshop.auth.service.EmailService;

import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;


@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    @Value("${application.email.username}")
    private String from;

    @Value("${application.email.frontend.confirm-url}")
    private String confirmUrl;

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Override
    public void sendEmail(String to,
                          EmailTemplateName emailTemplate,
                          String token,
                          String subject) {
        try {
            MimeMessage mimeMessage = createMimeMessage(
                    to,
                    emailTemplate,
                    token,
                    confirmUrl,
                    subject);
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new KvadroksException("Failed to send email to " + to, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private MimeMessage createMimeMessage(String to,
                                          EmailTemplateName emailTemplate,
                                          String token,
                                          String confirmUrl,
                                          String subject) throws MessagingException {
        Map<String, Object> properties = new HashMap<>();
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, UTF_8.name());
        String confirmationUrl;

        if (emailTemplate == EmailTemplateName.TWO_FACTOR) {
            confirmationUrl = String.format("%s/auth/confirm/2fa?email=%s&code=%s", confirmUrl, to, token);
            properties.put("code", token);
        } else {
            confirmationUrl = buildConfirmationUrl(confirmUrl, emailTemplate, token);
        }

        properties.put("confirm_url", confirmationUrl);

        Context context = new Context();
        context.setVariables(properties);

        helper.setFrom(from);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(getTemplateContent(emailTemplate, context), true);

        return mimeMessage;
    }

    private String buildConfirmationUrl(String confirmUrl, EmailTemplateName template, String token) {
        return switch (template) {
            case ACTIVATE_ACCOUNT -> String.format("%s/auth/confirm/register?token=%s", confirmUrl, token);
            case CHANGE_EMAIL -> String.format("%s/auth/confirm/change/email?token=%s", confirmUrl, token);
            case CHANGE_PASSWORD -> String.format("%s/auth/confirm/change/password?token=%s", confirmUrl, token);
            case RESET_PASSWORD -> String.format("%s/auth/confirm/reset/password?token=%s", confirmUrl, token);
            default -> throw new KvadroksException("Unexpected value: " + template, HttpStatus.INTERNAL_SERVER_ERROR);
        };
    }

    private String getTemplateContent(EmailTemplateName emailTemplate, Context context) {
        String templateName = switch (emailTemplate) {
            case ACTIVATE_ACCOUNT -> "activate_account";
            case CHANGE_EMAIL -> "change_email";
            case RESET_PASSWORD -> "reset_password";
            case CHANGE_PASSWORD -> "change_password";
            case TWO_FACTOR -> "two_factor_auth";
        };
        return templateEngine.process(templateName, context);
    }
}
