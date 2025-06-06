package ru.bakht.internetshop.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bakht.internetshop.exception.AppException;
import ru.bakht.internetshop.mapper.BaseProductFeatureMapper;
import ru.bakht.internetshop.model.Type;
import ru.bakht.internetshop.model.dto.TypeDto;
import ru.bakht.internetshop.repository.TypeRepo;
import ru.bakht.internetshop.service.BaseProductFeatureService;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class TypeServiceImpl implements BaseProductFeatureService<TypeDto> {

    private final TypeRepo typeRepo;
    private final BaseProductFeatureMapper<TypeDto, Type> typeMapper;

    @Override
    @Transactional(readOnly = true)
    public List<TypeDto> getAll() {
        return typeRepo.findAll().stream()
                .map(typeMapper::toDto)
                .toList();
    }

    @Override
    public void create(TypeDto typeDto) {
        var type = typeMapper.toEntity(typeDto);
        typeRepo.save(type);
    }

    @Override
    public void changeVisibility(UUID id, boolean isVisible) {
        var type = typeRepo.findById(id)
                .orElseThrow(() -> new AppException("Type not found with ID: " + id, HttpStatus.NOT_FOUND));
        type.setIsVisible(isVisible);
        typeRepo.save(type);
    }
}
