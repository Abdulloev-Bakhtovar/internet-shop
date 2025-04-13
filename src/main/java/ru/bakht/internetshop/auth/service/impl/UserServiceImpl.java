package ru.bakht.internetshop.auth.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bakht.internetshop.exception.AppException;
import ru.bakht.internetshop.auth.mapper.UserInfoMapper;
import ru.bakht.internetshop.auth.mapper.UserMapper;
import ru.bakht.internetshop.auth.model.User;
import ru.bakht.internetshop.auth.model.dto.UserDto;
import ru.bakht.internetshop.auth.model.dto.UserInfoDto;
import ru.bakht.internetshop.auth.repository.UserRepo;
import ru.bakht.internetshop.auth.service.UserService;
import ru.bakht.internetshop.auth.specification.UserInfoSpecification;
import ru.bakht.internetshop.auth.util.AuthUtils;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepo userRepository;
    private final UserRepo userRepo;
    private final UserInfoMapper userInfoMapper;
    private final AuthUtils authUtils;
    private final UserMapper userMapper;

    @Override
    public Page<UserDto> getAll(UserDto filter, int page, int size, String sortField, String sortDirection) {

        Pageable pageable = createPageRequest(page, size, sortField, sortDirection);
        Specification<User> specification = createSpecificationWithFilter(filter);

        Page<User> users = userRepository.findAll(specification, pageable);

        return users.map(userMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public User getByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException("User not found with email: " + email,
                        HttpStatus.NOT_FOUND)
                );
    }

    @Override
    public User getById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));
    }

    @Override
    public void validateUserDoesNotExist(String email) {
        if (userRepo.existsByEmail(email)) {
            throw new AppException(
                    "User already exists with email: " + email,
                    HttpStatus.CONFLICT
            );
        }
    }

    @Override
    @Transactional(readOnly = true)
    public UserInfoDto getUserInfo() {
        var user = getAuthenticatedAndValidatedUser();

        return userInfoMapper.toDto(user.getUserInfo());
    }

    @Override
    @Transactional
    public void updateUserInfo(UserInfoDto userInfoDto) {
        var user = getAuthenticatedAndValidatedUser();
        user.setUserInfo(userInfoMapper.updateEntity(userInfoDto, user.getUserInfo()));
        userRepository.save(user);
    }

    private User getAuthenticatedAndValidatedUser() {
        var authenticatedUser = authUtils.getAuthenticatedUser();

        return userRepository.findById(authenticatedUser.getId())
                .orElseThrow(() -> new AppException(
                        "User not found with user ID: " + authenticatedUser.getId(),
                        HttpStatus.NOT_FOUND
                ));
    }

    private Pageable createPageRequest(int page, int size, String sortField, String sortDirection) {
        Sort.Direction direction = Sort.Direction.fromOptionalString(sortDirection).orElse(Sort.Direction.DESC);
        return PageRequest.of(page, size, Sort.by(direction, sortField));
    }

    private Specification<User> createSpecificationWithFilter(UserDto filter) {
        return UserInfoSpecification.withFilter(filter);
    }
}
