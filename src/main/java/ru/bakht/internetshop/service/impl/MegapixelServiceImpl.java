package ru.bakht.internetshop.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bakht.internetshop.exception.AppException;
import ru.bakht.internetshop.mapper.BaseProductFeatureMapper;
import ru.bakht.internetshop.model.Megapixel;
import ru.bakht.internetshop.model.dto.MegapixelDto;
import ru.bakht.internetshop.repository.MegapixelRepo;
import ru.bakht.internetshop.service.BaseProductFeatureService;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MegapixelServiceImpl implements BaseProductFeatureService<MegapixelDto> {

    private final MegapixelRepo megapixelRepo;
    private final BaseProductFeatureMapper<MegapixelDto, Megapixel> megapixelMapper;

    @Override
    @Transactional(readOnly = true)
    public List<MegapixelDto> getAll() {
        return megapixelRepo.findAll().stream()
                .map(megapixelMapper::toDto)
                .toList();
    }

    @Override
    public void create(MegapixelDto megapixelDto) {
        var megapixel = megapixelMapper.toEntity(megapixelDto);
        megapixelRepo.save(megapixel);
    }

    @Override
    public void changeVisibility(UUID id, boolean isVisible) {
        var megapixel = megapixelRepo.findById(id)
                .orElseThrow(() -> new AppException("Megapixel not found with ID: " + id, HttpStatus.NOT_FOUND));
        megapixel.setIsVisible(isVisible);
        megapixelRepo.save(megapixel);
    }
}
