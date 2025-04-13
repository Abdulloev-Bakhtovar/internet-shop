package ru.bakht.internetshop.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.bakht.internetshop.auth.model.TokenType;
import ru.bakht.internetshop.auth.model.enums.TokenTypeName;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TokenTypeRepo extends JpaRepository<TokenType, UUID> {

    Optional<TokenType> findByName(TokenTypeName tokenTypeName);
}


