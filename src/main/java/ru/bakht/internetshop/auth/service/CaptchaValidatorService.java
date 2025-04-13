package ru.bakht.internetshop.auth.service;

public interface CaptchaValidatorService {

    boolean validate(String captchaResponse, String clientIp);
}
