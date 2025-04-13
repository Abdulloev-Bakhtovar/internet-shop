package ru.bakht.internetshop.auth.repository;

import java.util.Optional;

public interface BaseEmailRepo<T> {

    Optional<T> findByEmail(String email);

    boolean existsByEmail(String email);
}

