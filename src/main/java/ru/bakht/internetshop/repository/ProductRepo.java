package ru.bakht.internetshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.bakht.internetshop.model.Product;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepo extends JpaRepository<Product, UUID> {

    List<Product> findByIsVisibleTrue();
    Optional<Product> findByUniqueName(String uniqueName);
    boolean existsByUniqueName(String uniqueName);
}
