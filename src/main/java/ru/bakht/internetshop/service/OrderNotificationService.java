package ru.bakht.internetshop.service;

import ru.bakht.internetshop.model.OrderProductInfo;

import java.util.List;

public interface OrderNotificationService {

    void notifyAboutOrderCreation(List<OrderProductInfo> orderProductInfos);
}
