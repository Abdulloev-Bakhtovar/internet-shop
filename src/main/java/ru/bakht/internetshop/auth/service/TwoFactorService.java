package ru.bakht.internetshop.auth.service;

import jakarta.servlet.http.HttpServletResponse;
import ru.bakht.internetshop.auth.model.dto.TokenDto;

public interface TwoFactorService {

    void generateAndSendCode(String email);

    TokenDto verifyCode(String email, String code, HttpServletResponse response);

    void changeTwoFactor(boolean isTwoFactorEnabled);
}
