package ru.bakht.internetshop.auth.mapper;

import ru.bakht.internetshop.auth.model.UserInfo;
import ru.bakht.internetshop.auth.model.dto.UserInfoDto;

public interface UserInfoMapper {

    UserInfoDto toDto(UserInfo userInfo);

    UserInfo updateEntity(UserInfoDto userInfoDto, UserInfo userInfo);

    UserInfo toEntity(UserInfoDto userInfoDto);
}
