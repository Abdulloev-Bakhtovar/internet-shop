package ru.bakht.internetshop.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.bakht.internetshop.auth.model.Token;
import ru.bakht.internetshop.auth.model.TokenType;

import java.util.Optional;
import java.util.UUID;

public interface TokenRepo extends JpaRepository<Token, UUID> {

    Optional<Token> findByTokenAndTokenType(String token, TokenType tokenType);
}
