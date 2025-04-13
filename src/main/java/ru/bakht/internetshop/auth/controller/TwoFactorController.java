package ru.bakht.internetshop.auth.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.bakht.internetshop.auth.model.dto.TokenDto;
import ru.bakht.internetshop.auth.service.TwoFactorService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class TwoFactorController {

    private final TwoFactorService twoFactorService;

    @GetMapping("/confirm/2fa")
    @ResponseStatus(HttpStatus.OK)
    public TokenDto confirmTwoFactor(@RequestParam String email,
                                     @RequestParam String code,
                                     HttpServletResponse response) {
         return twoFactorService.verifyCode(email, code, response);
    }

    @PostMapping("/enable-2fa")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('USER')")
    public void enableTwoFactor() {
        twoFactorService.changeTwoFactor(true);
    }

    @PostMapping("/disable-2fa")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('USER')")
    public void disableTwoFactor() {
        twoFactorService.changeTwoFactor(false);
    }
}
