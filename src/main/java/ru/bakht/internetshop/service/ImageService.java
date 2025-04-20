package ru.bakht.internetshop.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import ru.bakht.internetshop.model.dto.ImageDto;

import java.util.List;
import java.util.UUID;

public interface ImageService {

    Resource getImage(UUID id);

    List<ImageDto> addImages(List<MultipartFile> images);

    ImageDto saveImage(MultipartFile imageFile);

    List<ImageDto> updateImages(List<ImageDto> images, List<MultipartFile> images1);
}
