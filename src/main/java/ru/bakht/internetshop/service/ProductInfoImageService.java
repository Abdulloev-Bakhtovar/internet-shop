package ru.bakht.internetshop.service;

import ru.bakht.internetshop.model.ProductInfo;
import ru.bakht.internetshop.model.dto.ImageDto;

import java.util.List;

public interface ProductInfoImageService {

    void create(List<ImageDto> imageDtos, ProductInfo productInfo);
}
