package ru.bakht.internetshop.mapper;

public interface BaseProductFeatureMapper<D, E> {

    D toDto(E entity);

    E toEntity(D dto);

    E toEntityWithId(D dto);
}
