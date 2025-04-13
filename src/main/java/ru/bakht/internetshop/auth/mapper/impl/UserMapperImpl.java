package ru.bakht.internetshop.auth.mapper.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.bakht.internetshop.auth.exception.KvadroksException;
import ru.bakht.internetshop.auth.mapper.UserInfoMapper;
import ru.bakht.internetshop.auth.mapper.UserMapper;
import ru.bakht.internetshop.auth.model.Role;
import ru.bakht.internetshop.auth.model.User;
import ru.bakht.internetshop.auth.model.UserInfo;
import ru.bakht.internetshop.auth.model.dto.EmailAndPassDto;
import ru.bakht.internetshop.auth.model.dto.UserDto;

import java.util.Collections;

@Component
@RequiredArgsConstructor
public class UserMapperImpl implements UserMapper {

    private final PasswordEncoder passwordEncoder;
    private final UserInfoMapper userMapper;

    @Override
    public UserDto toDto(User user) {

        if (user == null) {
            return null;
        }

        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .isTwoFactorEnabled(user.isTwoFactorEnabled())
                .createdDate(user.getCreatedDate())
                .updatedDate(user.getUpdatedDate())
                .userInfo(userMapper.toDto(user.getUserInfo()))
                .build();
    }

    @Override
    public User toEntity(EmailAndPassDto dto, Role role) {
        if (dto == null) {
            throw new KvadroksException("Dto cannot be null", HttpStatus.BAD_REQUEST);
        }

        return User.builder()
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .accountLocked(false)
                .emailVerified(false)
                .isTwoFactorEnabled(false)
                .userInfo(getUserInfo(dto.getEmail()))
                .roles(Collections.singletonList(role))
                .build();
    }

    private UserInfo getUserInfo(String email) {
        return UserInfo.builder()
                .email(email)
                .name("")
                .phone("")
                .address("")
                .build();
    }
}
