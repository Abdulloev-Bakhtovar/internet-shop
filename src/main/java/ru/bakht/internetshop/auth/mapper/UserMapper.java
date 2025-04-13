package ru.bakht.internetshop.auth.mapper;

import ru.bakht.internetshop.auth.model.Role;
import ru.bakht.internetshop.auth.model.User;
import ru.bakht.internetshop.auth.model.dto.EmailAndPassDto;
import ru.bakht.internetshop.auth.model.dto.UserDto;

public interface UserMapper {

    User toEntity(EmailAndPassDto dto, Role role);

    UserDto toDto(User user);
}
