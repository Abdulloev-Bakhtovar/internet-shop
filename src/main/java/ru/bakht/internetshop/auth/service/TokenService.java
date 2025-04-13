package ru.bakht.internetshop.auth.service;

import ru.bakht.internetshop.auth.model.User;
import ru.bakht.internetshop.auth.model.enums.TokenTypeName;

public interface TokenService {

    void save(User user, String generatedToken, TokenTypeName tokenTypeName);
}
