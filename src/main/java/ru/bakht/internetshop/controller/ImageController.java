package ru.bakht.internetshop.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.bakht.internetshop.model.dto.ImageDto;
import ru.bakht.internetshop.service.ImageService;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/cameras/images")
@RequiredArgsConstructor
@Tag(name = "Image", description = "Image API")
public class ImageController {

    private final ImageService imageService;

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping(consumes = {"multipart/form-data"})
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<List<ImageDto>> addImages(@RequestPart("images") List<MultipartFile> images) throws IOException {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(imageService.addImages(images));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Resource> getImage(@PathVariable UUID id) throws IOException {
        Resource resource = imageService.getImage(id);
        String mimeType = Files.probeContentType(resource.getFile().toPath());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, mimeType)
                .body(resource);
    }
}
