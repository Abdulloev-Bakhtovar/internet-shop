package ru.bakht.internetshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.bakht.internetshop.model.Type;

import java.util.UUID;

@Repository
public interface TypeRepository extends JpaRepository<Type, UUID> {
}
