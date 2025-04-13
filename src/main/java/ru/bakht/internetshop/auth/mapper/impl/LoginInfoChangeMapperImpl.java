package ru.bakht.internetshop.auth.mapper.impl;

import org.springframework.stereotype.Component;
import ru.bakht.internetshop.auth.mapper.LoginInfoChangeMapper;
import ru.bakht.internetshop.auth.model.LoginInfoChange;
import ru.bakht.internetshop.auth.model.User;
import ru.bakht.internetshop.auth.model.enums.ChangeType;

@Component
public class LoginInfoChangeMapperImpl implements LoginInfoChangeMapper {

    @Override
    public LoginInfoChange toEntity(ChangeType changeType, String value, User user) {

        return LoginInfoChange.builder()
                .changeType(changeType)
                .value(value)
                .user(user)
                .build();
    }
}
