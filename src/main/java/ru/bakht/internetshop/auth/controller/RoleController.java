package ru.bakht.internetshop.auth.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.bakht.internetshop.auth.model.dto.RoleDto;
import ru.bakht.internetshop.auth.service.RoleService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/roles")
@PreAuthorize("hasAuthority('ADMIN')")
public class RoleController {

    private final RoleService roleService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<RoleDto> getAll() {
        return roleService.getAll();
    }

    @PatchMapping("/add/{roleId}/{userId}")
    public void addRole(@PathVariable UUID roleId, @PathVariable UUID userId) {
        roleService.addRole(roleId, userId);
    }

    @PatchMapping("/remove/{roleId}/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeRole(@PathVariable UUID roleId, @PathVariable UUID userId) {
        roleService.removeRole(roleId, userId);
    }
}
