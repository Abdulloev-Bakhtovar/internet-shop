package ru.bakht.internetshop.mapper;

import ru.bakht.internetshop.model.Product;
import ru.bakht.internetshop.model.dto.ProductDto;

public interface ProductMapper {

    ProductDto toDto(Product product);
    void toEntity(ProductDto productDto, Product product);
    Product toEntity(ProductDto productDto);
}
