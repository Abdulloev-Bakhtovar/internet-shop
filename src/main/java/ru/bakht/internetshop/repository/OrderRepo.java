package ru.bakht.internetshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.bakht.internetshop.model.Order;

import java.util.List;
import java.util.UUID;

public interface OrderRepo extends
        JpaRepository<Order, UUID>,
        JpaSpecificationExecutor<Order> {
    List<Order> findAllByIdInOrderByOrderDateDesc(List<UUID> ids);
}
