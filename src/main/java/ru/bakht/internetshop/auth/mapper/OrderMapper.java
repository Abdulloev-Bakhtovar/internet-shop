package ru.bakht.internetshop.auth.mapper;

import ru.bakht.internetshop.model.Order;
import ru.bakht.internetshop.model.dto.OrderDto;

public interface OrderMapper {

    OrderDto toDto(Order order);

    Order toEntity(OrderDto orderDto);
}
