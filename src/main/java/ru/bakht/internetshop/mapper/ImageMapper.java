package ru.bakht.internetshop.mapper;

import org.springframework.web.multipart.MultipartFile;
import ru.bakht.internetshop.model.Image;
import ru.bakht.internetshop.model.ProductInfoImage;
import ru.bakht.internetshop.model.dto.ImageDto;

import java.util.List;

public interface ImageMapper {

    ImageDto toDto(Image image);
    Image toEntity(MultipartFile file);
    ImageDto toDto(ProductInfoImage productInfoImage);
    List<ImageDto> toDtoList(List<ProductInfoImage> productInfoImages);
}
