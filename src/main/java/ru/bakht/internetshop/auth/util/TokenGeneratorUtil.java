package ru.bakht.internetshop.auth.util;

import org.springframework.http.HttpStatus;
import ru.bakht.internetshop.auth.exception.KvadroksException;

import java.security.SecureRandom;
import java.util.Base64;

public class TokenGeneratorUtil {

    private static final SecureRandom secureRandom = new SecureRandom();

    /**
     * Генерирует числовой код указанной длины
     * @param length длина генерируемого кода (количество цифр)
     * @return сгенерированный числовой код
     * @throws IllegalArgumentException если длина меньше 1
     */
    public static String generateNumericCode(int length) throws KvadroksException {
        if (length < 1) {
            throw new KvadroksException("Длина кода должна быть положительным числом", HttpStatus.BAD_REQUEST);
        }

        StringBuilder code = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            code.append(secureRandom.nextInt(10));
        }

        return code.toString();
    }

    /**
     * Генерирует случайную строку указанной длины
     * @param length длина строки
     * @return случайная строка в Base64
     */
    public static String generateRandomString(int length) {
        byte[] randomBytes = new byte[length];
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }
}
