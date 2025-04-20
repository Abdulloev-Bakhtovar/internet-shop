package ru.bakht.internetshop.auth.mapper.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import ru.bakht.internetshop.auth.mapper.OrderProductInfoMapper;
import ru.bakht.internetshop.exception.AppException;
import ru.bakht.internetshop.mapper.ProductInfoMapper;
import ru.bakht.internetshop.model.Order;
import ru.bakht.internetshop.model.OrderProductInfo;
import ru.bakht.internetshop.model.dto.ProductDto;
import ru.bakht.internetshop.repository.ProductInfoRepo;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderProductInfoMapperImpl implements OrderProductInfoMapper {

    private final ProductInfoMapper productInfoMapper;
    private final ProductInfoRepo productInfoRepository;

    @Override
    public ProductDto toDto(OrderProductInfo entity) {
        if (entity == null){
            return null;
        }

        return ProductDto.builder()
                .id(entity.getProductInfo().getProduct().getId())
                .uniqueName(entity.getProductInfo().getProduct().getUniqueName())
                .isVisible(entity.getProductInfo().getProduct().getIsVisible())
                .productInfo(productInfoMapper.toDto(entity.getProductInfo()))
                .quantity(entity.getQuantity())
                .build();

    }

    @Override
    public List<ProductDto> toDtoList(List<OrderProductInfo> orderProductInfos) {
        if (orderProductInfos == null || orderProductInfos.isEmpty()){
            throw  new AppException("Order product infos is empty or null", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return orderProductInfos.stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public List<OrderProductInfo> toEntityList(List<ProductDto> productDtos, Order order) {

        return productDtos.stream()
                .map(productDto -> {
                    var productInfo = productInfoRepository.findById(productDto.getProductInfo().getId())
                            .orElseThrow(() -> new AppException("Product info not found with id: "
                                    + productDto.getProductInfo().getId(), HttpStatus.NOT_FOUND));

                    return OrderProductInfo.builder()
                            .id(new OrderProductInfo.OrderProductInfoId(order.getId(), productInfo.getId()))
                            .quantity(productDto.getQuantity())
                            .productInfo(productInfo)
                            .order(order)
                            .build();
                }).toList();
    }
}
