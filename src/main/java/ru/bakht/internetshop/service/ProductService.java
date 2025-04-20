package ru.bakht.internetshop.service;

import org.springframework.web.multipart.MultipartFile;
import ru.bakht.internetshop.model.dto.ProductDto;

import java.util.List;
import java.util.UUID;

public interface ProductService {

    List<ProductDto> getAllVisible();
    ProductDto getById(UUID id);
    ProductDto getByUniqueName(String uniqueName);
    List<ProductDto> getAll();
    void create(ProductDto productDto, List<MultipartFile> images);
    void updateProduct(ProductDto productDto, List<MultipartFile> images);
    void validateRelatedEntities(ProductDto productDto);
}
