package ru.bakht.internetshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.bakht.internetshop.model.ProductInfoImage;

public interface ProductInfoImageRepo
        extends JpaRepository<ProductInfoImage, ProductInfoImage.ProductInfoImageId> {
}
