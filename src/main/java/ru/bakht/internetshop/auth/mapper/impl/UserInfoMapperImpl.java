package ru.bakht.internetshop.auth.mapper.impl;

import org.springframework.stereotype.Component;
import ru.bakht.internetshop.auth.mapper.UserInfoMapper;
import ru.bakht.internetshop.auth.model.UserInfo;
import ru.bakht.internetshop.auth.model.dto.UserInfoDto;

@Component
public class UserInfoMapperImpl implements UserInfoMapper {

    @Override
    public UserInfoDto toDto(UserInfo userInfo) {
        if (userInfo == null) {
            return null;
        }

        return UserInfoDto.builder()
                .id(userInfo.getId())
                .name(userInfo.getName())
                .email(userInfo.getEmail())
                .phone(userInfo.getPhone())
                .address(userInfo.getAddress())
                .createdDate(userInfo.getCreatedDate())
                .updatedDate(userInfo.getUpdatedDate())
                .build();
    }

    @Override
    public UserInfo updateEntity(UserInfoDto userInfoDto, UserInfo userInfo) {
        if (userInfoDto == null) {
            return null;
        }

        userInfo.setEmail(userInfoDto.getEmail());
        userInfo.setAddress(userInfoDto.getAddress());
        userInfo.setPhone(userInfoDto.getPhone());
        userInfo.setName(userInfoDto.getName());

        return userInfo;
    }

    @Override
    public UserInfo toEntity(UserInfoDto userInfoDto) {
        if (userInfoDto == null) {
            return null;
        }

        return UserInfo.builder()
                .name(userInfoDto.getName())
                .email(userInfoDto.getEmail())
                .phone(userInfoDto.getPhone())
                .address(userInfoDto.getAddress())
                .build();
    }
}
