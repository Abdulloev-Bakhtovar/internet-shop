package ru.bakht.internetshop.mapper.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.bakht.internetshop.mapper.BaseProductFeatureMapper;
import ru.bakht.internetshop.mapper.ImageMapper;
import ru.bakht.internetshop.mapper.ProductInfoMapper;
import ru.bakht.internetshop.model.*;
import ru.bakht.internetshop.model.dto.MegapixelDto;
import ru.bakht.internetshop.model.dto.MillimeterDto;
import ru.bakht.internetshop.model.dto.ProductInfoDto;
import ru.bakht.internetshop.model.dto.TypeDto;
import ru.bakht.internetshop.repository.ProductInfoRepo;
import ru.bakht.internetshop.repository.ProductRepo;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ProductInfoMapperImpl implements ProductInfoMapper {

    private final ProductInfoRepo productInfoRepo;
    private final ProductRepo productRepo;
    private final BaseProductFeatureMapper<MillimeterDto, Millimeter> millimeterMapper;
    private final BaseProductFeatureMapper<MegapixelDto, Megapixel> megapixelMapper;
    private final BaseProductFeatureMapper<TypeDto, Type> typeMapper;
    private final ImageMapper imageMapper;

    @Override
    @Transactional(readOnly = true)
    public ProductInfoDto toDto(ProductInfo productInfo) {
        if (productInfo == null) {
            return null;
        }

        return ProductInfoDto.builder()
                .id(productInfo.getId())
                .name(productInfo.getName())
                .createDate(this.getCreateDate(productInfo))
                .updateDate(productInfo.getUpdateDate())
                .price(productInfo.getPrice())
                .discount(productInfo.getDiscount())
                .isInterestDiscount(productInfo.getIsInterestDiscount())
                .description(productInfo.getDescription())
                .millimeter(millimeterMapper.toDto(productInfo.getMillimeter()))
                .megapixel(megapixelMapper.toDto(productInfo.getMegapixel()))
                .type(typeMapper.toDto(productInfo.getType()))
                .images(imageMapper.toDtoList(productInfo.getProductInfoImages()))
                .build();
    }

    @Override
    public ProductInfo toEntity(ProductInfoDto productInfoDto, Product savedProduct) {
        if (productInfoDto == null) {
            return null;
        }

        return ProductInfo.builder()
                .name(productInfoDto.getName())
                .updateDate(Instant.now())
                .price(productInfoDto.getPrice())
                .discount(productInfoDto.getDiscount())
                .isInterestDiscount(productInfoDto.getIsInterestDiscount())
                .description(productInfoDto.getDescription())
                .millimeter(millimeterMapper.toEntityWithId(productInfoDto.getMillimeter()))
                .megapixel(megapixelMapper.toEntityWithId(productInfoDto.getMegapixel()))
                .type(typeMapper.toEntityWithId(productInfoDto.getType()))
                .product(savedProduct)
                .build();
    }

    private Instant getCreateDate(ProductInfo entity) {
        if (entity == null) {
            return null;
        }

        productRepo.findById(entity.getId());
        List<ProductInfo> productInfos = productInfoRepo.findByProductId(entity.getProduct().getId());

        if (!productInfos.isEmpty()) {
            return productInfos.stream()
                    .min(Comparator.comparing(ProductInfo::getUpdateDate))
                    .get().getUpdateDate();
        }

        return null;
    }
}
