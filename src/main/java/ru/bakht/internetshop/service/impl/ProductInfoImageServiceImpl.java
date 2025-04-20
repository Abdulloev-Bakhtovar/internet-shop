package ru.bakht.internetshop.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bakht.internetshop.model.ProductInfo;
import ru.bakht.internetshop.model.ProductInfoImage;
import ru.bakht.internetshop.model.dto.ImageDto;
import ru.bakht.internetshop.repository.ImageRepo;
import ru.bakht.internetshop.repository.ProductInfoImageRepo;
import ru.bakht.internetshop.service.ProductInfoImageService;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ProductInfoImageServiceImpl implements ProductInfoImageService {

    private final ProductInfoImageRepo productInfoImageRepo;
    private final ImageRepo imageRepo;

    @Override
    public void create(List<ImageDto> imageDtos, ProductInfo productInfo) {

        var counter = new AtomicInteger();
        List<ProductInfoImage> productInfoImages = imageDtos.stream()
                .map(imageDto -> {
                    var image = imageRepo.findById(imageDto.getId())
                            .orElse(null);

                    if (image != null) {
                        return ProductInfoImage.builder()
                                .id(new ProductInfoImage.ProductInfoImageId(productInfo.getId(), image.getId()))
                                .image(image)
                                .productInfo(productInfo)
                                .number(counter.incrementAndGet())
                                .build();
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        productInfoImageRepo.saveAll(productInfoImages);
    }

}
