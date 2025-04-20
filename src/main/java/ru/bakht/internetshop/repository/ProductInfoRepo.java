package ru.bakht.internetshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.bakht.internetshop.model.ProductInfo;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductInfoRepo extends JpaRepository<ProductInfo, UUID> {

    List<ProductInfo> findByProductId(UUID productId);
}
