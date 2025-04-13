package ru.bakht.internetshop.auth.service;

import org.springframework.data.domain.Page;
import ru.bakht.internetshop.auth.model.User;
import ru.bakht.internetshop.auth.model.dto.UserDto;
import ru.bakht.internetshop.auth.model.dto.UserInfoDto;

import java.util.UUID;

public interface UserService {

    Page<UserDto> getAll(UserDto filter, int page, int size, String sortField, String sortDirection);

    User getByEmail(String email);

    User getById(UUID id);

    void validateUserDoesNotExist(String email);

    UserInfoDto getUserInfo();

    void updateUserInfo(UserInfoDto userInfo);
}
