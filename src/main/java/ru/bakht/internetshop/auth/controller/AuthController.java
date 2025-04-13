package ru.bakht.internetshop.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.bakht.internetshop.auth.model.dto.EmailAndPassDto;
import ru.bakht.internetshop.auth.model.dto.TokenDto;
import ru.bakht.internetshop.auth.service.AuthService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@RequestBody EmailAndPassDto registerDto, HttpServletRequest request) {
        authService.register(registerDto, request);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public TokenDto login(@RequestBody EmailAndPassDto loginDto,
                          HttpServletRequest request,
                          HttpServletResponse response) {
        return authService.login(loginDto, request, response);
    }

    @GetMapping("/refresh")
    @ResponseStatus(HttpStatus.OK)
    public TokenDto refreshToken(HttpServletRequest request, HttpServletResponse response) {
        return authService.refreshToken(request, response);
    }

    @GetMapping("/logout")
    @ResponseStatus(HttpStatus.OK)
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        authService.logout(request, response);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAuthority('USER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAccount(HttpServletResponse response) {
        authService.delete(response);
    }
}
