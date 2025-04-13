package ru.bakht.internetshop.mapper.impl;

import org.springframework.stereotype.Component;
import ru.bakht.internetshop.mapper.BaseProductFeatureMapper;
import ru.bakht.internetshop.model.Millimeter;
import ru.bakht.internetshop.model.dto.MillimeterDto;

@Component
public class MillimeterMapperImpl implements BaseProductFeatureMapper<MillimeterDto, Millimeter> {

    @Override
    public MillimeterDto toDto(Millimeter entity) {
        if (entity == null) {
            return null;
        }

        return MillimeterDto.builder()
                .id(entity.getId())
                .value(entity.getValue())
                .isVisible(entity.getIsVisible())
                .build();
    }

    @Override
    public Millimeter toEntity(MillimeterDto dto) {
        if (dto == null) {
            return null;
        }

        return Millimeter.builder()
                .value(dto.getValue())
                .isVisible(dto.getIsVisible())
                .build();
    }

    @Override
    public Millimeter toEntityWithId(MillimeterDto dto) {
        if (dto == null) {
            return null;
        }

        return Millimeter.builder()
                .id(dto.getId())
                .value(dto.getValue())
                .isVisible(dto.getIsVisible())
                .build();
    }
}
