package ru.bakht.internetshop.auth.service;

import ru.bakht.internetshop.auth.model.Role;
import ru.bakht.internetshop.auth.model.dto.RoleDto;
import ru.bakht.internetshop.auth.model.enums.RoleName;

import java.util.List;
import java.util.UUID;

public interface RoleService {

    List<RoleDto> getAll();

    Role getByName(RoleName roleName);

    void addRole(UUID roleId, UUID userId);

    void removeRole(UUID roleId, UUID userId);
}
