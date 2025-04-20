package ru.bakht.internetshop.mapper;

import ru.bakht.internetshop.model.Product;
import ru.bakht.internetshop.model.ProductInfo;
import ru.bakht.internetshop.model.dto.ProductInfoDto;

public interface ProductInfoMapper {

    ProductInfoDto toDto(ProductInfo productInfo);

    ProductInfo toEntity(ProductInfoDto productInfoDto, Product product);
}
