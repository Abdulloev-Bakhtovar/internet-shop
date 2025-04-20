package ru.bakht.internetshop.service;

import org.springframework.data.domain.Page;
import ru.bakht.internetshop.model.dto.OrderDto;
import ru.bakht.internetshop.model.dto.OrderFilterDto;

import java.util.List;
import java.util.UUID;

public interface OrderService {
    Page<OrderDto> getUserOrders(OrderFilterDto criteria, int page, int size, String sortField, String sortDirection);
    Page<OrderDto> getAllOrders(OrderFilterDto criteria, int page, int size, String sortField, String sortDirection);
    List<OrderDto> getOrdersByIds(List<UUID> orderIds);
    UUID createOrder(OrderDto orderDto);
}
