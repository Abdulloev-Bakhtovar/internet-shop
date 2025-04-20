package ru.bakht.internetshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.bakht.internetshop.model.OrderProductInfo;

public interface OrderProductInfoRepo extends JpaRepository<OrderProductInfo, OrderProductInfo.OrderProductInfoId> {
}
