package ru.bakht.internetshop.auth.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.bakht.internetshop.auth.model.dto.ResetPasswordDto;
import ru.bakht.internetshop.auth.model.enums.TokenTypeName;

public interface ConfirmTokenService {

    void confirmTokenFromEmail(String token,
                               TokenTypeName tokenTypeName,
                               HttpServletRequest request,
                               HttpServletResponse response
    );

    void confirmTokenFromEmailAndResetPass(ResetPasswordDto resetPasswordDto,
                                           String token,
                                           TokenTypeName tokenTypeName
    );
}