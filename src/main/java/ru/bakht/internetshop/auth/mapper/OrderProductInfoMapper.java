package ru.bakht.internetshop.auth.mapper;

import ru.bakht.internetshop.model.Order;
import ru.bakht.internetshop.model.OrderProductInfo;
import ru.bakht.internetshop.model.dto.ProductDto;

import java.util.List;

public interface OrderProductInfoMapper {

    ProductDto toDto(OrderProductInfo orderProductInfo);

    List<ProductDto> toDtoList(List<OrderProductInfo> orderProductInfos);

    List<OrderProductInfo> toEntityList(List<ProductDto> productDtos, Order order);
}
