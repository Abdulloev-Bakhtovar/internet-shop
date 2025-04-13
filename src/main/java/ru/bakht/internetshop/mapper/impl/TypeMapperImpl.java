package ru.bakht.internetshop.mapper.impl;

import org.springframework.stereotype.Component;
import ru.bakht.internetshop.mapper.BaseProductFeatureMapper;
import ru.bakht.internetshop.model.Type;
import ru.bakht.internetshop.model.dto.TypeDto;

@Component
public class TypeMapperImpl implements BaseProductFeatureMapper<TypeDto, Type> {

    @Override
    public TypeDto toDto(Type entity) {
        if (entity == null) {
            return null;
        }

        return TypeDto.builder()
                .id(entity.getId())
                .value(entity.getValue())
                .isVisible(entity.getIsVisible())
                .build();
    }

    @Override
    public Type toEntity(TypeDto dto) {
        if (dto == null) {
            return null;
        }

        return Type.builder()
                .value(dto.getValue())
                .isVisible(dto.getIsVisible())
                .build();
    }

    @Override
    public Type toEntityWithId(TypeDto dto) {
        if (dto == null) {
            return null;
        }

        return Type.builder()
                .id(dto.getId())
                .value(dto.getValue())
                .isVisible(dto.getIsVisible())
                .build();
    }
}
