package ru.bakht.internetshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.bakht.internetshop.model.Image;

import java.util.UUID;

@Repository
public interface ImageRepo extends JpaRepository<Image, UUID> {
}
