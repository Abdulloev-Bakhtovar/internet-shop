package ru.bakht.internetshop.mapper.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.bakht.internetshop.auth.util.AuthUtils;
import ru.bakht.internetshop.mapper.ProductInfoMapper;
import ru.bakht.internetshop.mapper.ProductMapper;
import ru.bakht.internetshop.model.Cart;
import ru.bakht.internetshop.model.Product;
import ru.bakht.internetshop.model.ProductInfo;
import ru.bakht.internetshop.model.dto.ProductDto;
import ru.bakht.internetshop.repository.CartRepo;

import java.util.Comparator;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ProductMapperImpl implements ProductMapper {

    private final ProductInfoMapper productInfoMapper;
    private final AuthUtils authUtils;
    private final CartRepo cartRepocartRepo;

    @Override
    @Transactional(readOnly = true)
    public ProductDto toDto(Product product) {
        // Находим самый первый ProductInfo
        ProductInfo mostFirstProductInfo = product.getProductInfos().stream()
                .min(Comparator.comparing(ProductInfo::getUpdateDate))
                .orElse(null);

        // Находим самый последний (свежий) ProductInfo
        ProductInfo mostRecentProductInfo = product.getProductInfos().stream()
                .max(Comparator.comparing(ProductInfo::getUpdateDate))
                .orElse(null);

        if (mostRecentProductInfo != null) {
            // Маппим в ProductDto
            ProductDto dto = this.toDto(product, mostRecentProductInfo);

            // Устанавливаем дату создания как дату самого первого ProductInfo
            dto.getProductInfo().setCreateDate(mostFirstProductInfo.getUpdateDate());

            return dto;
        }

        return null;
    }

    @Override
    public void toEntity(ProductDto productDto, Product product) {
        if (productDto == null) {
            return;
        }

        product.setUniqueName(productDto.getUniqueName());
        product.setIsVisible(productDto.getIsVisible());
        product.setVideoUrl(productDto.getVideoUrl());
    }

    @Override
    public Product toEntity(ProductDto productDto) {
        if (productDto == null) {
            return null;
        }

        return Product.builder()
                .uniqueName(productDto.getUniqueName())
                .isVisible(productDto.getIsVisible())
                .videoUrl(productDto.getVideoUrl())
                .build();
    }

    private ProductDto toDto(Product product, ProductInfo mostRecentProductInfo) {
        if (product == null) {
            return null;
        }

        return ProductDto.builder()
                .id(product.getId())
                .uniqueName(product.getUniqueName())
                .isVisible(product.getIsVisible())
                .videoUrl(product.getVideoUrl())
                .productInfo(productInfoMapper.toDto(mostRecentProductInfo))
                .quantity(this.getQuantity(product.getId()))
                .build();
    }

    private long getQuantity(UUID productId) {
        var user = authUtils.getAuthenticatedUser();

        if (user == null) {
            return 0L;
        }

        var cart = cartRepocartRepo.findByUserIdAndProductId(user.getId(), productId)
                .orElse(Cart.builder()
                        .quantity(0L)
                        .build()
                );

        return cart.getQuantity();
    }

}
