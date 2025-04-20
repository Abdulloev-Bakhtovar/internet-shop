package ru.bakht.internetshop.service.impl;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.bakht.internetshop.auth.model.enums.EmailTemplateName;
import ru.bakht.internetshop.auth.service.EmailService;
import ru.bakht.internetshop.model.Order;
import ru.bakht.internetshop.model.OrderProductInfo;
import ru.bakht.internetshop.service.OrderNotificationService;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderNotificationServiceImpl implements OrderNotificationService {

    @Value("${application.email.order.notification.username}")
    private String orderNotificationEmail;

    private final EmailService emailService;

    @Override
    public void notifyAboutOrderCreation(List<OrderProductInfo> orderProductInfos) {
        // Получение заказа из информации о продуктах
        if (orderProductInfos.isEmpty()) {
            log.warn("No products in the order to notify about.");
            return;
        }

        Order order = orderProductInfos.getFirst().getOrder(); // Предполагаем, что все продукты из одного заказа
        String subject = "Новый заказ: " + order.getId();
        String emailContent = buildOrderEmailContent(orderProductInfos);

        try {
            emailService.sendEmail(
                    orderNotificationEmail,
                    EmailTemplateName.ORDER_NOTIFICATION,
                    null,
                    subject,
                    orderProductInfos
            );
        } catch (MessagingException e) {
            log.error("Error sending notify about order creation: {}", e.getMessage());
        }

        log.info("Order notification email sent to {}", orderNotificationEmail);
    }

    private String buildOrderEmailContent(List<OrderProductInfo> orderProductInfos) {
        Order order = orderProductInfos.get(0).getOrder();
        StringBuilder content = new StringBuilder();
        content.append("Новый заказ был создан:\n\n")
                .append("ID заказа: ").append(order.getId()).append("\n")
                .append("Дата заказа: ").append(order.getOrderDate()).append("\n")
                .append("Описание: ").append(order.getDescription()).append("\n\n")
                .append("Информация о клиенте:\n")
                .append("Имя: ").append(order.getUserInfo().getName()).append("\n")
                .append("Email: ").append(order.getUserInfo().getEmail()).append("\n")
                .append("Телефон: ").append(order.getUserInfo().getPhone()).append("\n")
                .append("Адрес: ").append(order.getUserInfo().getAddress()).append("\n\n")
                .append("Товары в заказе:\n\n");

        BigDecimal totalSum = BigDecimal.ZERO;

        for (OrderProductInfo productInfo : orderProductInfos) {
            BigDecimal price = productInfo.getProductInfo().getPrice();
            long quantity = productInfo.getQuantity();
            BigDecimal discount = productInfo.getProductInfo().getDiscount();
            boolean isInterestDiscount = productInfo.getProductInfo().getIsInterestDiscount();

            // Рассчет скидки
            BigDecimal effectivePrice = isInterestDiscount
                    ? price.subtract(price.multiply(discount.divide(BigDecimal.valueOf(100))))
                    : price.subtract(discount);

            // Умножение цены с учетом скидки на количество
            BigDecimal productTotal = effectivePrice.multiply(BigDecimal.valueOf(quantity));
            totalSum = totalSum.add(productTotal);

            // Добавление информации о продукте в контент письма
            content.append("Продукт: ").append(productInfo.getProductInfo().getProduct().getUniqueName()).append("\n")
                    .append("Название: ").append(productInfo.getProductInfo().getName()).append("\n")
                    .append("Цена: ").append(price).append("\n")
                    .append("Скидка: ").append(discount).append("\n")
                    .append("Скидка по процентам: ").append(isInterestDiscount).append("\n")
                    .append("Миллиметры: ").append(productInfo.getProductInfo().getMillimeter().getValue()).append("\n")
                    .append("Мегапиксели: ").append(productInfo.getProductInfo().getMegapixel().getValue()).append("\n")
                    .append("Тип: ").append(productInfo.getProductInfo().getType().getValue()).append("\n")
                    .append("Количество: ").append(quantity).append("\n")
                    .append("Сумма за этот продукт: ").append(productTotal).append("\n\n");
        }

        // Итоговая сумма для всего заказа
        content.append("Итоговая сумма заказа: ").append(totalSum).append("\n");

        return content.toString();
    }


}
