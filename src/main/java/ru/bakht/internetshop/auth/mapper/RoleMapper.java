package ru.bakht.internetshop.auth.mapper;

import ru.bakht.internetshop.auth.model.Role;
import ru.bakht.internetshop.auth.model.dto.RoleDto;

public interface RoleMapper {

    RoleDto toDto(Role role);
}
