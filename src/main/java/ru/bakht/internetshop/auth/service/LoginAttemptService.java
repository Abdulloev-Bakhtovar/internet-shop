package ru.bakht.internetshop.auth.service;

public interface LoginAttemptService {

    void loginFailed(String username);

    boolean isAccountLocked(String username);

    void loginSuccess(String username);

    String getAccountUnlockTime(String username);

    boolean isCaptchaRequired(String email);
}
