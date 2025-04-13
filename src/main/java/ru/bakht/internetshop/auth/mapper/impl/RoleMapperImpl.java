package ru.bakht.internetshop.auth.mapper.impl;

import org.springframework.stereotype.Component;
import ru.bakht.internetshop.auth.mapper.RoleMapper;
import ru.bakht.internetshop.auth.model.Role;
import ru.bakht.internetshop.auth.model.dto.RoleDto;

@Component
public class RoleMapperImpl implements RoleMapper {

    @Override
    public RoleDto toDto(Role role) {
        if (role == null) return null;

        return RoleDto.builder()
                .id(role.getId())
                .name(role.getName())
                .build();
    }
}
