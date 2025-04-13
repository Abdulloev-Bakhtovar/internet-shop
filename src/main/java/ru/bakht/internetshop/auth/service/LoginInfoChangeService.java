package ru.bakht.internetshop.auth.service;

import ru.bakht.internetshop.auth.model.LoginInfoChange;
import ru.bakht.internetshop.auth.model.User;
import ru.bakht.internetshop.auth.model.dto.ChangePasswordDto;
import ru.bakht.internetshop.auth.model.dto.EmailDto;
import ru.bakht.internetshop.auth.model.dto.EmailAndPassDto;
import ru.bakht.internetshop.auth.model.enums.ChangeType;

public interface LoginInfoChangeService {

    void changeEmail(EmailAndPassDto changeEmailDto);

    void changePassword(ChangePasswordDto changePasswordDto);

    void resetPasswordRequest(EmailDto emailDto);

    LoginInfoChange getByUserAndChangeType(User user, ChangeType changeType);

    void validateUserDoesNotExist(String email);
}
