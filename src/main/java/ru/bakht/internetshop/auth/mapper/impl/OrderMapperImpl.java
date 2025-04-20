package ru.bakht.internetshop.auth.mapper.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.bakht.internetshop.auth.mapper.OrderMapper;
import ru.bakht.internetshop.auth.mapper.OrderProductInfoMapper;
import ru.bakht.internetshop.auth.mapper.UserInfoMapper;
import ru.bakht.internetshop.model.Order;
import ru.bakht.internetshop.model.dto.OrderDto;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class OrderMapperImpl implements OrderMapper {

    private final UserInfoMapper userInfoMapper;
    private final OrderProductInfoMapper orderProductInfoMapper;

    @Override
    public OrderDto toDto(Order order) {
        if (order == null) {
            return null;
        }

        return OrderDto.builder()
                .id(order.getId())
                .orderDate(order.getOrderDate())
                .description(order.getDescription())
                .userInfo(userInfoMapper.toDto(order.getUserInfo()))
                .products(orderProductInfoMapper.toDtoList(order.getOrderProductInfos()))
                .build();
    }

    @Override
    public Order toEntity(OrderDto orderDto) {
        if (orderDto == null) {
            return null;
        }

        return Order.builder()
                .orderDate(Instant.now())
                .description(orderDto.getDescription())
                .build();
    }
}
