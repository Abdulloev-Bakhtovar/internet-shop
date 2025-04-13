package ru.bakht.internetshop.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.bakht.internetshop.auth.model.dto.ResetPasswordDto;
import ru.bakht.internetshop.auth.model.enums.TokenTypeName;
import ru.bakht.internetshop.auth.service.ConfirmTokenService;

@RestController
@RequestMapping("/auth/confirm")
@RequiredArgsConstructor
public class ConfirmTokenFromEmailController {

    private final ConfirmTokenService confirmTokenService;

    @GetMapping("/register")
    @ResponseStatus(HttpStatus.OK)
    public void confirmRegisterToken(
            @RequestParam String token,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        confirmTokenService.confirmTokenFromEmail(token, TokenTypeName.ACTIVATION, request, response);
    }

    @GetMapping("/change/email")
    @ResponseStatus(HttpStatus.OK)
    public void confirmEmailChangeToken(
            @RequestParam String token,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        confirmTokenService.confirmTokenFromEmail(token, TokenTypeName.EMAIL_CHANGE, request, response);
    }

    @GetMapping("/change/password")
    @ResponseStatus(HttpStatus.OK)
    public void confirmPasswordChangeToken(
            @RequestParam String token,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        confirmTokenService.confirmTokenFromEmail(token, TokenTypeName.PASSWORD_CHANGE, request, response);
    }

    @PatchMapping("/reset-password")
    @ResponseStatus(HttpStatus.OK)
    public void confirmResetPassword(@RequestParam String token, @RequestBody ResetPasswordDto resetPasswordDto) {
        confirmTokenService.confirmTokenFromEmailAndResetPass(resetPasswordDto, token, TokenTypeName.PASSWORD_RESET);
    }
}