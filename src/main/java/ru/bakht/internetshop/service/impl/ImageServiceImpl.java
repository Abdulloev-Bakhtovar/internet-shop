package ru.bakht.internetshop.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.bakht.internetshop.exception.AppException;
import ru.bakht.internetshop.mapper.ImageMapper;
import ru.bakht.internetshop.model.Image;
import ru.bakht.internetshop.model.dto.ImageDto;
import ru.bakht.internetshop.repository.ImageRepo;
import ru.bakht.internetshop.service.ImageService;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    @Value("${application.image.path}")
    private String imageDirectory;

    private final ImageRepo imageRepo;
    private final ImageMapper imageMapper;

    @Override
    public List<ImageDto> addImages(List<MultipartFile> images) {
        log.info("Attempting to add {} images", images.size());

        var imageEntities = images.stream()
                .map(imageMapper::toEntity)
                .toList();

        imageRepo.saveAll(imageEntities);

        for (int i = 0; i < imageEntities.size(); i++) {
            try {
                saveImageToFileSystem(imageEntities.get(i), images.get(i).getBytes());
            } catch (IOException e) {
                log.error("Failed to save image to file system for image with ID: {}", imageEntities.get(i).getId(), e);
                throw new AppException("Failed to save image to file system", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return imageEntities.stream()
                .map(imageMapper::toDto)
                .toList();
    }

    @Override
    public List<ImageDto> updateImages(List<ImageDto> imageIds, List<MultipartFile> images) {
        log.info("Attempting to update {} images", images.size());

        var counter  = new AtomicInteger();

        return imageIds.stream()
                .map(imageDto -> {
                    if (imageDto.getId() == null && counter.get() < images.size()) {
                        MultipartFile imageFile = images.get(counter.getAndIncrement());
                        return saveImage(imageFile);
                    } else {
                        return imageDto;
                    }
                })
                .toList();
    }

    @Override
    public Resource getImage(UUID id) {
        log.info("Attempting to retrieve image with ID: {}", id);

        var image = imageRepo.findById(id)
                .orElseThrow(() -> {
                    log.error("Image not found with ID: {}", id);
                    return new AppException("Image not found with ID: " + id, HttpStatus.NOT_FOUND);
                });

        String extension = image.getMimeType().split("/")[1];
        Path imagePath = Paths.get(imageDirectory, image.getId().toString() + "." + extension);

        if (Files.exists(imagePath)) {
            try {
                log.info("Image with ID: {} found, returning resource", id);
                return new UrlResource(imagePath.toUri());
            } catch (MalformedURLException e) {
                log.error("Malformed URL for image with ID: {}", id, e);
                throw new AppException("Invalid URL for image with ID: " + id, HttpStatus.BAD_REQUEST);
            }
        } else {
            log.error("Image file not found on disk for image with ID: {}", id);
            throw new AppException("Image file not found on disk for image with ID: " + id, HttpStatus.NOT_FOUND);
        }
    }

    @Override
    @Transactional
    public ImageDto saveImage(MultipartFile imageFile) {
        log.info("Attempting to add image");

        try {
            var image = imageMapper.toEntity(imageFile);
            imageRepo.save(image);
            saveImageToFileSystem(image, imageFile.getBytes());

            return imageMapper.toDto(image);
        } catch (IOException e) {
            log.error("Failed to save image to file system", e);
            throw new AppException("Failed to save image to file system", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void saveImageToFileSystem(Image image, byte[] imageBytes) {
        log.info("Attempting to save image to file system");

        String extension = image.getMimeType().split("/")[1];
        Path imagePath = Paths.get(imageDirectory, image.getId().toString() + "." + extension);

        try {
            Files.createDirectories(imagePath.getParent());
            Files.write(imagePath, imageBytes);
        } catch (IOException e) {
            log.error("Failed to save image to file system with path: {}", imagePath, e);
            throw new AppException("Failed to save image to file system", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
