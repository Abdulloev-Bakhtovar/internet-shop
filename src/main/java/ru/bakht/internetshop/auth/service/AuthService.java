package ru.bakht.internetshop.auth.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.bakht.internetshop.auth.model.dto.EmailAndPassDto;
import ru.bakht.internetshop.auth.model.dto.TokenDto;

public interface AuthService {

    void register(EmailAndPassDto registerDto, HttpServletRequest request);

    TokenDto login(EmailAndPassDto loginDto, HttpServletRequest request, HttpServletResponse response);

    TokenDto refreshToken(HttpServletRequest request, HttpServletResponse response);

    void logout(HttpServletRequest request, HttpServletResponse response);

    void delete(HttpServletResponse response);
}