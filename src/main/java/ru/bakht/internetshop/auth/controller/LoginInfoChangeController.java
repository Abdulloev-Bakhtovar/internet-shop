package ru.bakht.internetshop.auth.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.bakht.internetshop.auth.model.dto.ChangePasswordDto;
import ru.bakht.internetshop.auth.model.dto.EmailDto;
import ru.bakht.internetshop.auth.model.dto.EmailAndPassDto;
import ru.bakht.internetshop.auth.service.LoginInfoChangeService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class LoginInfoChangeController {

    private final LoginInfoChangeService changeService;

    @PatchMapping("/change/email")
    @PreAuthorize("hasAuthority('USER')")
    @ResponseStatus(HttpStatus.OK)
    public void changeEmail(@RequestBody EmailAndPassDto changeEmail) {
        changeService.changeEmail(changeEmail);
    }


    @PatchMapping("/change/password")
    @PreAuthorize("hasAuthority('USER')")
    @ResponseStatus(HttpStatus.OK)
    public void changePassword(@RequestBody ChangePasswordDto changePasswordDto) {
        changeService.changePassword(changePasswordDto);
    }

    @PatchMapping("/reset/password")
    @ResponseStatus(HttpStatus.OK)
    public void resetPasswordRequest(@RequestBody EmailDto emailDto) {
        changeService.resetPasswordRequest(emailDto);
    }
}
