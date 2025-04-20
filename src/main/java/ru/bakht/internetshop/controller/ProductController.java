package ru.bakht.internetshop.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.bakht.internetshop.model.dto.*;
import ru.bakht.internetshop.service.ProductService;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/cameras")
@RequiredArgsConstructor
@Tag(name = "Product", description = "Product API")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<ProductDto>> getAllVisibleProducts() {
        return ResponseEntity.ok(productService.getAllVisible());
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/id/{cameraId}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable UUID cameraId) {
        return ResponseEntity.ok(productService.getById(cameraId));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PatchMapping(value = "/change/{id}",consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Void> updateProduct(
            @PathVariable UUID id,
            @RequestParam String name,
            @RequestParam BigDecimal price,
            @RequestParam BigDecimal discount,
            @RequestParam Boolean isInterestDiscount,
            @RequestParam String description,
            @RequestParam UUID millimeterId,
            @RequestParam UUID megapixelId,
            @RequestParam UUID typeId,
            @RequestParam String uniqueName,
            @RequestParam Boolean isVisible,
            @RequestParam(required = false) String videoUrl,
            @RequestParam List<String> imageIds,
            @RequestPart(value = "images", required = false) List<MultipartFile> images
    ) {

        List<ImageDto> imageDtos = imageIds == null ? Collections.emptyList() : imageIds.stream()
                .map(imageIdStr -> {
                    if ("null".equals(imageIdStr)) {
                        return ImageDto.builder().id(null).build();
                    } else {
                        return ImageDto.builder().id(UUID.fromString(imageIdStr)).build();
                    }
                })
                .collect(Collectors.toList());


        var productInfoDto = ProductInfoDto.builder()
                .name(name)
                .price(price)
                .discount(discount)
                .isInterestDiscount(isInterestDiscount)
                .description(description)
                .millimeter(MillimeterDto.builder()
                        .id(millimeterId)
                        .build())
                .megapixel(MegapixelDto.builder()
                        .id(megapixelId)
                        .build())
                .type(TypeDto.builder()
                        .id(typeId)
                        .build())
                .images(imageDtos)
                .build();
        var productDto = ProductDto.builder()
                .id(id)
                .uniqueName(uniqueName)
                .isVisible(isVisible)
                .videoUrl(videoUrl)
                .productInfo(productInfoDto)
                .build();

        productService.updateProduct(productDto, images == null ? Collections.emptyList() : images);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/name/{unName}")
    public ResponseEntity<ProductDto> getProductByUniqueName(@PathVariable String unName) {
        return ResponseEntity.ok(productService.getByUniqueName(unName));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<List<ProductDto>> getAllProducts() {
        return ResponseEntity.ok(productService.getAll());
    }

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> createProduct(
            @RequestParam String name,
            @RequestParam BigDecimal price,
            @RequestParam BigDecimal discount,
            @RequestParam Boolean isInterestDiscount,
            @RequestParam String description,
            @RequestParam UUID millimeterId,
            @RequestParam UUID megapixelId,
            @RequestParam UUID typeId,
            @RequestParam String uniqueName,
            @RequestParam Boolean isVisible,
            @RequestParam(required = false) String videoUrl,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {

        var productInfoDto = ProductInfoDto.builder()
                .name(name)
                .price(price)
                .discount(discount)
                .isInterestDiscount(isInterestDiscount)
                .description(description)
                .millimeter(MillimeterDto.builder()
                        .id(millimeterId)
                        .build())
                .megapixel(MegapixelDto.builder()
                        .id(megapixelId)
                        .build())
                .type(TypeDto.builder()
                        .id(typeId)
                        .build())
                .build();
        var productDto = ProductDto.builder()
                .uniqueName(uniqueName)
                .isVisible(isVisible)
                .videoUrl(videoUrl)
                .productInfo(productInfoDto)
                .build();
        productService.create(productDto, images);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
