package ru.bakht.internetshop.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.bakht.internetshop.auth.model.LoginInfoChange;
import ru.bakht.internetshop.auth.model.User;
import ru.bakht.internetshop.auth.model.enums.ChangeType;

import java.util.Optional;
import java.util.UUID;

public interface LoginInfoChangeRepo extends JpaRepository<LoginInfoChange, UUID> {

    Optional<LoginInfoChange> findByUserIdAndChangeType(UUID userId, ChangeType changeType);

    void deleteAllByUserAndChangeType(User user, ChangeType changeType);

    boolean existsByValue(String value);
}
