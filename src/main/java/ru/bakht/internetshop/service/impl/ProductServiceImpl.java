package ru.bakht.internetshop.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.bakht.internetshop.exception.AppException;
import ru.bakht.internetshop.mapper.ProductInfoMapper;
import ru.bakht.internetshop.mapper.ProductMapper;
import ru.bakht.internetshop.model.ProductInfo;
import ru.bakht.internetshop.model.dto.ImageDto;
import ru.bakht.internetshop.model.dto.ProductDto;
import ru.bakht.internetshop.repository.*;
import ru.bakht.internetshop.service.AuditService;
import ru.bakht.internetshop.service.ImageService;
import ru.bakht.internetshop.service.ProductInfoImageService;
import ru.bakht.internetshop.service.ProductService;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepo productRepository;
    private final ProductMapper productMapper;
    private final MillimeterRepo millimeterRepository;
    private final MegapixelRepo megapixelRepository;
    private final TypeRepo typeRepository;
    private final ProductInfoMapper productInfoMapper;
    private final ImageService imageService;
    private final ProductInfoRepo productInfoRepository;
    private final ProductInfoImageService productInfoImageService;
    private final AuditService auditService;

    @Override
    public List<ProductDto> getAllVisible() {
        log.info("Attempting to retrieve all visible products");
        return productRepository.findByIsVisibleTrue().stream()
                .map(productMapper::toDto)
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    public ProductDto getById(UUID id) {
        log.info("Attempting to retrieve product by id: {}", id);
        return productRepository.findById(id).stream()
                .map(productMapper::toDto)
                .filter(Objects::nonNull)
                .findFirst()
                .orElseThrow(() -> new AppException("Product not found with ID: " + id, HttpStatus.NOT_FOUND));
    }

    @Override
    public ProductDto getByUniqueName(String uniqueName) {
        log.info("Attempting to retrieve product by unique name: {}", uniqueName);
        return productRepository.findByUniqueName(uniqueName).stream()
                .map(productMapper::toDto)
                .filter(Objects::nonNull)
                .findFirst()
                .orElseThrow(() -> new AppException("Product not found with uniqueName: " + uniqueName,
                                                          HttpStatus.NOT_FOUND));
    }

    @Override
    public List<ProductDto> getAll() {
        log.info("Attempting to retrieve all products");
        return productRepository.findAll().stream()
                .map(productMapper::toDto)
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    public void create(ProductDto productDto, List<MultipartFile> images) {

        if (images == null || images.isEmpty()) {
            log.error("Images cannot be null or empty");
            throw new AppException("Images cannot be blank", HttpStatus.BAD_REQUEST);
        }

        validateRelatedEntities(productDto);

        if (productRepository.existsByUniqueName(productDto.getUniqueName())) {
            log.error("Product already exists with unique name: {}", productDto.getUniqueName());
            throw new AppException("Product with the same unique name already exists", HttpStatus.CONFLICT);
        }

        var product = productMapper.toEntity(productDto);
        product = productRepository.save(product);

        var productInfo = productInfoMapper.toEntity(productDto.getProductInfo(), product);
        productInfo = productInfoRepository.save(productInfo);

        List<ImageDto> imageDtos = imageService.addImages(images);

        productInfoImageService.create(imageDtos, productInfo);

        auditService.logAction(
                this.getClass().getName() + "." + new Object(){}.getClass().getEnclosingMethod().getName(),
                product.getClass().getName(),
                product.getId(),
                "Created new product"
        );
    }

    @Override
    public void updateProduct(ProductDto productDto, List<MultipartFile> images) {
        log.info("Attempting to update product info for product ID: {}", productDto.getId());

        validateRelatedEntities(productDto);

        var productEntity = productRepository.findById(productDto.getId())
                .orElseThrow(() -> {
                    log.error("Product not found with ID: {}", productDto.getId());
                    return new AppException("Product not found with ID: " + productDto.getId(), HttpStatus.NOT_FOUND);
                });

        var productWithSameUniqueName = productRepository.findByUniqueName(productDto.getUniqueName())
                .orElse(null);
        if (productWithSameUniqueName != null && !productWithSameUniqueName.getId().equals(productDto.getId())) {
            log.error("Product already exists with unique name: {}", productWithSameUniqueName);
            throw new AppException("Product with the same unique name already exists", HttpStatus.CONFLICT);
        }

        if (!productDto.getUniqueName().equals(productEntity.getUniqueName())
                || productDto.getIsVisible() != productEntity.getIsVisible()
                || !productDto.getVideoUrl().equals(productEntity.getVideoUrl())
        ) {
            productMapper.toEntity(productDto, productEntity);

            productEntity = productRepository.save(productEntity);
        }

        var mostRecentProductInfo = productEntity.getProductInfos().stream()
                .max(Comparator.comparing(ProductInfo::getUpdateDate))
                .orElseThrow(() -> {
                    log.error("Product info not found for product with ID {}", productDto.getId());
                    return new AppException("Product info not found for product with ID: " + productDto.getId(),
                            HttpStatus.NOT_FOUND);
                });

        var productInfo = productInfoMapper.toEntity(productDto.getProductInfo(), mostRecentProductInfo.getProduct());
        productInfo = productInfoRepository.save(productInfo);

        List<ImageDto> imageDtos = imageService.updateImages(productDto.getProductInfo().getImages(), images);

        productInfoImageService.create(imageDtos, productInfo);

        auditService.logAction(
                this.getClass().getName() + "." + new Object(){}.getClass().getEnclosingMethod().getName(),
                productInfo.getClass().getName(),
                productInfo.getId(),
                "Updated product info for product with ID: " + productEntity.getId()
        );
    }

    @Override
    public void validateRelatedEntities(ProductDto productDto) {
        validateEntityExists(millimeterRepository, productDto.getProductInfo().getMillimeter().getId(), "Millimeter");
        validateEntityExists(megapixelRepository, productDto.getProductInfo().getMegapixel().getId(), "Megapixel");
        validateEntityExists(typeRepository, productDto.getProductInfo().getType().getId(), "Camera type");
    }

    private <T> void validateEntityExists(JpaRepository<T, UUID> repository, UUID id, String entityName) {
        if (!repository.existsById(id)) {
            log.error("{} not found with ID: {}", entityName, id);
            throw new AppException(entityName + " not found with ID: " + id, HttpStatus.NOT_FOUND);
        }
    }
}
