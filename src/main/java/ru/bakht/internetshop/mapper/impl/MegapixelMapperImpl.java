package ru.bakht.internetshop.mapper.impl;

import org.springframework.stereotype.Component;
import ru.bakht.internetshop.mapper.BaseProductFeatureMapper;
import ru.bakht.internetshop.model.Megapixel;
import ru.bakht.internetshop.model.dto.MegapixelDto;

@Component
public class MegapixelMapperImpl implements BaseProductFeatureMapper<MegapixelDto, Megapixel> {

    @Override
    public MegapixelDto toDto(Megapixel entity) {
        if (entity == null) {
            return null;
        }

        return MegapixelDto.builder()
                .id(entity.getId())
                .value(entity.getValue())
                .isVisible(entity.getIsVisible())
                .build();
    }

    @Override
    public Megapixel toEntity(MegapixelDto dto) {
        if (dto == null) {
            return null;
        }

        return Megapixel.builder()
                .value(dto.getValue())
                .isVisible(dto.getIsVisible())
                .build();
    }

    @Override
    public Megapixel toEntityWithId(MegapixelDto dto) {
        if (dto == null) {
            return null;
        }

        return Megapixel.builder()
                .id(dto.getId())
                .value(dto.getValue())
                .isVisible(dto.getIsVisible())
                .build();
    }
}
