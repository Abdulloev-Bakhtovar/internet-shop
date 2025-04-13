package ru.bakht.internetshop.auth.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bakht.internetshop.auth.mapper.TokenMapper;
import ru.bakht.internetshop.auth.model.User;
import ru.bakht.internetshop.auth.model.enums.TokenTypeName;
import ru.bakht.internetshop.auth.repository.TokenRepo;
import ru.bakht.internetshop.auth.service.TokenService;
import ru.bakht.internetshop.auth.service.TokenTypeService;

@Service
@Transactional
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    private final TokenRepo tokenRepo;
    private final TokenTypeService tokenTypeService;
    private final TokenMapper tokenMapper;

    @Override
    public void save(User user, String generatedToken, TokenTypeName tokenTypeName) {

        var tokenType = tokenTypeService.getByName(tokenTypeName);
        var token = tokenMapper.toEntity(generatedToken, tokenType, user);

        tokenRepo.save(token);
    }
}
