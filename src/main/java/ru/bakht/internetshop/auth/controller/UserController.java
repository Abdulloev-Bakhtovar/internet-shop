package ru.bakht.internetshop.auth.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.bakht.internetshop.auth.model.dto.UserDto;
import ru.bakht.internetshop.auth.model.dto.UserInfoDto;
import ru.bakht.internetshop.auth.service.UserService;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    public Page<UserDto> getAll(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String address,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdDate") String sortField,
            @RequestParam(defaultValue = "DESC") String sortDirection) {

        UserDto filter = UserDto.builder()
                .email(email)
                .userInfo(UserInfoDto.builder()
                        .name(name)
                        .phone(phone)
                        .address(address)
                        .build())
                .build();

        return userService.getAll(filter, page, size, sortField, sortDirection);
    }

    @GetMapping("/user-info")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('USER')")
    public UserInfoDto getUserInfo() {
        return userService.getUserInfo();
    }

    @PatchMapping("/user-info")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('USER')")
    public void updateUserInfo(@RequestBody UserInfoDto userInfo) {
        userService.updateUserInfo(userInfo);
    }
}
