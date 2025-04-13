package ru.bakht.internetshop.auth.mapper.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.bakht.internetshop.auth.mapper.TokenMapper;
import ru.bakht.internetshop.auth.model.Token;
import ru.bakht.internetshop.auth.model.TokenType;
import ru.bakht.internetshop.auth.model.User;
import ru.bakht.internetshop.auth.model.dto.TokenDto;

import java.time.Duration;
import java.time.Instant;

@Component
public class TokenMapperImpl implements TokenMapper {

    @Value("${application.security.jwt.confirm-token.expiration}")
    private int confirmTokenExpiration;

    @Override
    public Token toEntity(String generatedToken, TokenType tokenType, User user) {

        Instant createdDate = Instant.now();
        Instant expiresDate = createdDate.plus(Duration.ofMinutes(confirmTokenExpiration));

        return Token.builder()
                .token(generatedToken)
                .createdDate(createdDate)
                .expiresDate(expiresDate)
                .tokenType(tokenType)
                .user(user)
                .build();
    }

    @Override
    public TokenDto toDto(String accessToken) {

        return TokenDto.builder()
                .accessToken(accessToken)
                .build();
    }
}
