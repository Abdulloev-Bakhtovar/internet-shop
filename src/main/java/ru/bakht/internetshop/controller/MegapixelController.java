package ru.bakht.internetshop.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.bakht.internetshop.model.dto.TypeDto;
import ru.bakht.internetshop.service.BaseProductFeatureService;

@RestController
@RequestMapping("/cameras/megapixel")
public class MegapixelController extends AbstractProductFeatureController<TypeDto> {

    public MegapixelController(BaseProductFeatureService<TypeDto> service) {
        super(service);
    }
}
