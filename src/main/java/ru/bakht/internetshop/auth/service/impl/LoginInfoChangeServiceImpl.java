package ru.bakht.internetshop.auth.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bakht.internetshop.exception.AppException;
import ru.bakht.internetshop.auth.mapper.LoginInfoChangeMapper;
import ru.bakht.internetshop.auth.model.LoginInfoChange;
import ru.bakht.internetshop.auth.model.User;
import ru.bakht.internetshop.auth.model.dto.ChangePasswordDto;
import ru.bakht.internetshop.auth.model.dto.EmailAndPassDto;
import ru.bakht.internetshop.auth.model.dto.EmailDto;
import ru.bakht.internetshop.auth.model.enums.ChangeType;
import ru.bakht.internetshop.auth.model.enums.EmailTemplateName;
import ru.bakht.internetshop.auth.repository.LoginInfoChangeRepo;
import ru.bakht.internetshop.auth.service.LoginInfoChangeService;
import ru.bakht.internetshop.auth.service.NotificationService;
import ru.bakht.internetshop.auth.service.UserService;
import ru.bakht.internetshop.auth.util.AuthUtils;


@Service
@Transactional
@RequiredArgsConstructor
public class LoginInfoChangeServiceImpl implements LoginInfoChangeService {

    private final UserService userService;
    private final NotificationService notificationService;
    private final AuthUtils authUtils;
    private final LoginInfoChangeRepo loginInfoChangeRepo;
    private final LoginInfoChangeMapper loginInfoChangeMapper;

    public void changeEmail(EmailAndPassDto changeEmailDto) {
        User user = getValidatedUserForChange(changeEmailDto.getPassword());
        this.validateUserDoesNotExist(changeEmailDto.getEmail());
        userService.validateUserDoesNotExist(changeEmailDto.getEmail());

        saveLoginInfoChange(ChangeType.EMAIL, changeEmailDto.getEmail(), user);
        notificationService.sendEmailForUserAction(changeEmailDto.getEmail(), EmailTemplateName.CHANGE_EMAIL, user);
    }

    public void changePassword(ChangePasswordDto changePasswordDto) {
        var user = getValidatedUserForChange(changePasswordDto.getOldPassword());

        saveLoginInfoChange(ChangeType.PASSWORD, changePasswordDto.getNewPassword(), user);
        notificationService.sendEmailForUserAction(user.getEmail(), EmailTemplateName.CHANGE_PASSWORD, user);
    }

    @Override
    public void resetPasswordRequest(EmailDto emailDto) {
        User user = userService.getByEmail(emailDto.getEmail());
        notificationService.sendEmailForUserAction(user.getEmail(), EmailTemplateName.RESET_PASSWORD, user);
    }

    @Override
    public LoginInfoChange getByUserAndChangeType(User user, ChangeType changeType) {
        return loginInfoChangeRepo.findByUserIdAndChangeType(user.getId(), changeType)
                .orElseThrow(() -> new AppException(changeType + " not found", HttpStatus.NOT_FOUND));
    }

    @Override
    public void validateUserDoesNotExist(String email) {
        if(loginInfoChangeRepo.existsByValue(email)) {
            throw new AppException(
                    "User already exists with email: " + email,
                    HttpStatus.CONFLICT
            );
        }
    }

    private User getValidatedUserForChange(String password) {
        User user = authUtils.getAuthenticatedUser();
        user = userService.getById(user.getId());
        AuthUtils.validateCredentials(user.getEmail(), password);
        return user;
    }

    private void saveLoginInfoChange(ChangeType changeType, String newValue, User user) {
        var change = loginInfoChangeMapper.toEntity(changeType, newValue, user);
        loginInfoChangeRepo.deleteAllByUserAndChangeType(user, changeType);

        loginInfoChangeRepo.save(change);
    }
}
