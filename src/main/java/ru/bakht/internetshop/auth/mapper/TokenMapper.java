package ru.bakht.internetshop.auth.mapper;

import ru.bakht.internetshop.auth.model.Token;
import ru.bakht.internetshop.auth.model.TokenType;
import ru.bakht.internetshop.auth.model.User;
import ru.bakht.internetshop.auth.model.dto.TokenDto;

public interface TokenMapper {

    Token toEntity(String generatedToken, TokenType tokenType, User user);

    TokenDto toDto(String accessToken);
}
