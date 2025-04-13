package ru.bakht.internetshop.auth.mapper;

import ru.bakht.internetshop.auth.model.LoginInfoChange;
import ru.bakht.internetshop.auth.model.User;
import ru.bakht.internetshop.auth.model.enums.ChangeType;

public interface LoginInfoChangeMapper {

    LoginInfoChange toEntity(ChangeType changeType, String value, User user);
}
