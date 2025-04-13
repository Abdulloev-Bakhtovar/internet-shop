package ru.bakht.internetshop.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.bakht.internetshop.auth.model.Role;
import ru.bakht.internetshop.auth.model.enums.RoleName;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoleRepo extends JpaRepository<Role, UUID> {

    Optional<Role> findByName(RoleName roleName);
}
