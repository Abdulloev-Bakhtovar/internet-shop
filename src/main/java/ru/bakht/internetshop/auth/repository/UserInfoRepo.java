package ru.bakht.internetshop.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ru.bakht.internetshop.auth.model.UserInfo;

import java.util.UUID;

@Repository
public interface UserInfoRepo extends
        JpaRepository<UserInfo, UUID>,
        JpaSpecificationExecutor<UserInfo>,
        BaseEmailRepo<UserInfo> {

}
