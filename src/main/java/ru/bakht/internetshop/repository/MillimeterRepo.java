package ru.bakht.internetshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.bakht.internetshop.model.Millimeter;

import java.util.UUID;

@Repository
public interface MillimeterRepo extends JpaRepository<Millimeter, UUID> {
}
