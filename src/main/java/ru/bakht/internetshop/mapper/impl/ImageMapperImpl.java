package ru.bakht.internetshop.mapper.impl;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import ru.bakht.internetshop.exception.AppException;
import ru.bakht.internetshop.mapper.ImageMapper;
import ru.bakht.internetshop.model.Image;
import ru.bakht.internetshop.model.ProductInfoImage;
import ru.bakht.internetshop.model.dto.ImageDto;

import java.util.List;
import java.util.Objects;

@Component
public class ImageMapperImpl implements ImageMapper {

    @Override
    public ImageDto toDto(Image image) {
        if (image == null) {
            return null;
        }

        return ImageDto.builder()
                .id(image.getId())
                .build();
    }

    @Override
    public Image toEntity(MultipartFile file) {
        if (file == null) {
            return null;
        }

        return Image.builder()
                .name(extractFileNameWithoutExtension(Objects.requireNonNull(file.getOriginalFilename())))
                .mimeType(file.getContentType())
                .build();
    }

    @Override
    public ImageDto toDto(ProductInfoImage productInfoImage) {
        if (productInfoImage == null) {
            return null;
        }

        return ImageDto.builder()
                .id(productInfoImage.getImage().getId())
                .build();
    }

    @Override
    public List<ImageDto> toDtoList(List<ProductInfoImage> productInfoImages) {
        if (productInfoImages == null) {
            return null;
        }

        return productInfoImages.stream()
                .map(this::toDto)
                .toList();
    }

    private String extractFileNameWithoutExtension(String fileName) {
        int index = fileName.lastIndexOf('.');
        if (index > 0) {
            return fileName.substring(0, index);
        } else {
            throw new AppException("Invalid file name", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
