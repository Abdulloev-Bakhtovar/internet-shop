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
import ru.bakht.internetshop.auth.model.enums.EmailTemplateName;
import ru.bakht.internetshop.auth.service.EmailService;
import ru.bakht.internetshop.exception.AppException;
import ru.bakht.internetshop.model.Order;
import ru.bakht.internetshop.model.OrderProductInfo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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
                          String subject,
                          List<OrderProductInfo> orderProductInfos) {
        try {
            MimeMessage mimeMessage = createMimeMessage(
                    to,
                    emailTemplate,
                    token,
                    confirmUrl,
                    subject,
                    orderProductInfos);
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new AppException("Failed to send email to " + to, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private MimeMessage createMimeMessage(String to,
                                          EmailTemplateName emailTemplate,
                                          String token,
                                          String confirmUrl,
                                          String subject,
                                          List<OrderProductInfo> orderProductInfos) throws MessagingException {
        Map<String, Object> properties = new HashMap<>();
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, UTF_8.name());
        String confirmationUrl;

        if (orderProductInfos != null && !orderProductInfos.isEmpty()) {
            properties.putAll(buildOrderProperties(orderProductInfos));
        } else {

            if (emailTemplate == EmailTemplateName.TWO_FACTOR) {
                confirmationUrl = String.format("%s/auth/confirm/2fa?email=%s&code=%s", confirmUrl, to, token);
                properties.put("code", token);
            } else {
                confirmationUrl = buildConfirmationUrl(confirmUrl, emailTemplate, token);
            }

            properties.put("confirm_url", confirmationUrl);
        }

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
            default -> throw new AppException("Unexpected value: " + template, HttpStatus.INTERNAL_SERVER_ERROR);
        };
    }

    private String getTemplateContent(EmailTemplateName emailTemplate, Context context) {
        String templateName = switch (emailTemplate) {
            case ACTIVATE_ACCOUNT -> "activate_account";
            case CHANGE_EMAIL -> "change_email";
            case RESET_PASSWORD -> "reset_password";
            case CHANGE_PASSWORD -> "change_password";
            case TWO_FACTOR -> "two_factor_auth";
            case ORDER_NOTIFICATION -> "order_notification";
        };
        return templateEngine.process(templateName, context);
    }

    private Map<String, Object> buildOrderProperties(List<OrderProductInfo> orderProductInfos) {
        Map<String, Object> properties = new HashMap<>();
        Order order = orderProductInfos.getFirst().getOrder();

        String formattedOrderDate = formatOrderDateToUserTimeZone(order.getOrderDate(), "Asia/Yekaterinburg");

        properties.put("orderDate", formattedOrderDate);
        properties.put("order", order);
        properties.put("orderProductInfos", orderProductInfos);

        BigDecimal totalSum = orderProductInfos.stream()
                .map(productInfo -> {
                    BigDecimal price = productInfo.getProductInfo().getPrice();
                    BigDecimal discount = productInfo.getProductInfo().getDiscount();
                    boolean isInterestDiscount = productInfo.getProductInfo().getIsInterestDiscount();

                    BigDecimal effectivePrice = isInterestDiscount
                            ? price.subtract(price.multiply(discount))
                            : price.subtract(discount);
                    return effectivePrice.multiply(BigDecimal.valueOf(productInfo.getQuantity()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        properties.put("totalSum", totalSum);


        return properties;
    }

    public String formatOrderDateToUserTimeZone(Instant orderDateInstant, String userTimeZone) {
        ZonedDateTime localOrderDate = orderDateInstant.atZone(ZoneId.of(userTimeZone));

        return localOrderDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm", Locale.getDefault()));
    }
}
