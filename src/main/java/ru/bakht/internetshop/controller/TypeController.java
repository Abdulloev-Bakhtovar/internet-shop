package ru.bakht.internetshop.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.bakht.internetshop.model.dto.TypeDto;
import ru.bakht.internetshop.service.BaseProductFeatureService;

@RestController
@RequestMapping("/cameras/types")
public class TypeController extends AbstractProductFeatureController<TypeDto> {

    public TypeController(BaseProductFeatureService<TypeDto> service) {
        super(service);
    }
}
