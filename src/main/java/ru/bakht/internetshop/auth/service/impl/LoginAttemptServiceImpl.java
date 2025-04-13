package ru.bakht.internetshop.auth.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.bakht.internetshop.exception.AppException;
import ru.bakht.internetshop.auth.service.LoginAttemptService;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class LoginAttemptServiceImpl implements LoginAttemptService {

    private final RedisTemplate<String, String> redisTemplate;

    @Value("${application.security.login.max-attempts}")
    private int maxAttempts;

    @Value("${application.security.login.lock-duration-minutes}")
    private long lockDurationMinutes;

    @Value("${application.security.login.captcha-threshold}")
    private int captchaThreshold;

    private static final String LOGIN_ATTEMPTS_PREFIX = "login_attempts:";
    private static final String ACCOUNT_LOCK_PREFIX = "account_lock:";

    @Override
    public void loginFailed(String username) {
        try {
            String attemptsKey = LOGIN_ATTEMPTS_PREFIX + username;

            Long attempts = redisTemplate.opsForValue().increment(attemptsKey);

            if (attempts != null && attempts == 1) {
                redisTemplate.expire(attemptsKey, lockDurationMinutes, TimeUnit.MINUTES);
            }

            if (attempts != null && attempts >= maxAttempts) {
                lockAccount(username);
            }
        } catch (Exception e) {
            throw new AppException("Failed to process login attempt", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public boolean isAccountLocked(String username) {
        try {
            return redisTemplate.hasKey(ACCOUNT_LOCK_PREFIX + username);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void loginSuccess(String username) {
        try {
            redisTemplate.delete(LOGIN_ATTEMPTS_PREFIX + username);
        } catch (Exception e) {
            throw new AppException("Failed to reset login attempts", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public String getAccountUnlockTime(String username) {
        try {
            return redisTemplate.opsForValue().get(ACCOUNT_LOCK_PREFIX + username);
        } catch (Exception e) {
            return "Account is not locked";
        }
    }

    @Override
    public boolean isCaptchaRequired(String username) {
        String attempts = redisTemplate.opsForValue().get(LOGIN_ATTEMPTS_PREFIX + username);
        return attempts != null && Integer.parseInt(attempts) >= captchaThreshold;
    }

    private void lockAccount(String username) {
        Instant now = Instant.now();
        Instant unlockTime = now.plus(Duration.ofMinutes(lockDurationMinutes));

        DateTimeFormatter formatter = DateTimeFormatter
                .ofPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(ZoneId.systemDefault());

        String lockValue = String.format("Account locked. Will be unlocked at: %s",
                formatter.format(unlockTime));

        redisTemplate.opsForValue().set(
                ACCOUNT_LOCK_PREFIX + username,
                lockValue,
                lockDurationMinutes,
                TimeUnit.MINUTES
        );

        redisTemplate.delete(LOGIN_ATTEMPTS_PREFIX + username);
    }
}