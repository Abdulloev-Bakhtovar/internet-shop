package ru.bakht.internetshop.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.bakht.internetshop.model.dto.MegapixelDto;
import ru.bakht.internetshop.service.BaseProductFeatureService;

@RestController
@RequestMapping("/cameras/megapixel")
public class MegapixelController extends AbstractProductFeatureController<MegapixelDto> {

    public MegapixelController(BaseProductFeatureService<MegapixelDto> service) {
        super(service);
    }
}
