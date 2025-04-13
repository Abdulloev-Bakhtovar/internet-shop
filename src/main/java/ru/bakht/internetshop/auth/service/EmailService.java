package ru.bakht.internetshop.auth.service;

import jakarta.mail.MessagingException;
import org.springframework.scheduling.annotation.Async;
import ru.bakht.internetshop.auth.model.enums.EmailTemplateName;

public interface EmailService {

    @Async
    void sendEmail (String to,
                    EmailTemplateName emailTemplate,
                    String activationCode,
                    String subject) throws MessagingException;
}
