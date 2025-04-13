package ru.bakht.internetshop.service;

import java.util.List;
import java.util.UUID;

public interface BaseProductFeatureService<T> {

    List<T> getAll();

    void create(T featureDto);

    void changeVisibility(UUID id, boolean isVisible);
}
