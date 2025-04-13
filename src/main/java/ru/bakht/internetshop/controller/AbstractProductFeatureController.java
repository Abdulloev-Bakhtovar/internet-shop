package ru.bakht.internetshop.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.bakht.internetshop.service.BaseProductFeatureService;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ADMIN')")
public abstract class AbstractProductFeatureController<T> {

    private final BaseProductFeatureService<T> service;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<T> getAll() {
        return service.getAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@RequestBody T dto) {
        service.create(dto);
    }

    @PatchMapping("/{id}/hidden")
    @ResponseStatus(HttpStatus.OK)
    public void hide(@PathVariable UUID id) {
        service.changeVisibility(id, false);
    }

    @PatchMapping("/{id}/visible")
    @ResponseStatus(HttpStatus.OK)
    public void show(@PathVariable UUID id) {
        service.changeVisibility(id, true);
    }
}
