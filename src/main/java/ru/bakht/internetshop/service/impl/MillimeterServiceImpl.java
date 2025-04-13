package ru.bakht.internetshop.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bakht.internetshop.exception.AppException;
import ru.bakht.internetshop.mapper.BaseProductFeatureMapper;
import ru.bakht.internetshop.model.Millimeter;
import ru.bakht.internetshop.model.dto.MillimeterDto;
import ru.bakht.internetshop.repository.MillimeterRepo;
import ru.bakht.internetshop.service.BaseProductFeatureService;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MillimeterServiceImpl implements BaseProductFeatureService<MillimeterDto> {

    private final MillimeterRepo millimeterRepo;
    private final BaseProductFeatureMapper<MillimeterDto, Millimeter> millimeterMapper;

    @Override
    @Transactional(readOnly = true)
    public List<MillimeterDto> getAll() {
        return millimeterRepo.findAll().stream()
                .map(millimeterMapper::toDto)
                .toList();
    }

    @Override
    public void create(MillimeterDto millimeterDto) {
        var millimeter = millimeterMapper.toEntity(millimeterDto);
        millimeterRepo.save(millimeter);
    }

    @Override
    public void changeVisibility(UUID id, boolean isVisible) {
        var millimeter = millimeterRepo.findById(id)
                .orElseThrow(() -> new AppException("Millimeter not found with ID: " + id, HttpStatus.NOT_FOUND));
        millimeter.setIsVisible(isVisible);
        millimeterRepo.save(millimeter);
    }
}
