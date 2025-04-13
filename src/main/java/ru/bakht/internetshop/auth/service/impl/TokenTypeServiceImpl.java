package ru.bakht.internetshop.auth.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bakht.internetshop.auth.exception.KvadroksException;
import ru.bakht.internetshop.auth.model.TokenType;
import ru.bakht.internetshop.auth.model.enums.TokenTypeName;
import ru.bakht.internetshop.auth.repository.TokenTypeRepo;
import ru.bakht.internetshop.auth.service.TokenTypeService;


@Service
@Transactional
@RequiredArgsConstructor
public class TokenTypeServiceImpl implements TokenTypeService {

    private final TokenTypeRepo tokenTypeRepo;

    @Override
    @Transactional(readOnly = true)
    public TokenType getByName(TokenTypeName tokenTypeName) {

        return tokenTypeRepo.findByName(tokenTypeName)
                .orElseThrow(() -> new KvadroksException(
                            "Token type not found with name: " + tokenTypeName, HttpStatus.NOT_FOUND
                ));
    }
}
