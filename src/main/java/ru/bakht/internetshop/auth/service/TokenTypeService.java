package ru.bakht.internetshop.auth.service;

import ru.bakht.internetshop.auth.model.TokenType;
import ru.bakht.internetshop.auth.model.enums.TokenTypeName;

public interface TokenTypeService {

    TokenType getByName(TokenTypeName tokenTypeName);
}
