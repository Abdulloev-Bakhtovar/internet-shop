package ru.bakht.internetshop.auth.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class KvadroksException extends RuntimeException {

    private final HttpStatus status;

    public KvadroksException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}
