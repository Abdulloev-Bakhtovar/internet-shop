package ru.bakht.internetshop.auth.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bakht.internetshop.auth.exception.KvadroksException;
import ru.bakht.internetshop.auth.mapper.RoleMapper;
import ru.bakht.internetshop.auth.model.Role;
import ru.bakht.internetshop.auth.model.User;
import ru.bakht.internetshop.auth.model.dto.RoleDto;
import ru.bakht.internetshop.auth.model.enums.RoleName;
import ru.bakht.internetshop.auth.repository.RoleRepo;
import ru.bakht.internetshop.auth.service.RoleService;
import ru.bakht.internetshop.auth.service.UserService;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepo roleRepo;
    private final RoleMapper roleMapper;
    private final UserService userService;

    @Override
    @Transactional(readOnly = true)
    public List<RoleDto> getAll() {
        return roleRepo.findAll().stream()
                .map(roleMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Role getByName(RoleName roleName) {
        return roleRepo.findByName(roleName)
                .orElseThrow(() -> new KvadroksException(
                        "Role not found with name: " + roleName.name(), HttpStatus.NOT_FOUND)
                );
    }

    @Override
    public void addRole(UUID roleId, UUID userId) {
        Role role = roleRepo.findById(roleId)
                .orElseThrow(() -> new KvadroksException(
                        "Role not found with ID: " + roleId,
                        HttpStatus.NOT_FOUND
                ));

        User user = userService.getById(userId);

        if (user.getRoles().contains(role)) {
            throw new KvadroksException(
                    "User already has this role",
                    HttpStatus.CONFLICT
            );
        }

        user.getRoles().add(role);
    }

    @Override
    public void removeRole(UUID roleId, UUID userId) {
        Role role = roleRepo.findById(roleId)
                .orElseThrow(() -> new KvadroksException(
                        "Role not found with ID: " + roleId,
                        HttpStatus.NOT_FOUND
                ));

        User user = userService.getById(userId);

        if (!user.getRoles().remove(role)) {
            throw new KvadroksException(
                    "User does not have this role",
                    HttpStatus.BAD_REQUEST
            );
        }
    }
}
