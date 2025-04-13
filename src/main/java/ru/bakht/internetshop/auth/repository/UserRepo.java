package ru.bakht.internetshop.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ru.bakht.internetshop.auth.model.User;

import java.util.UUID;

@Repository
public interface UserRepo extends
        JpaRepository<User, UUID>,
        BaseEmailRepo<User>,
        JpaSpecificationExecutor<User> {

}
