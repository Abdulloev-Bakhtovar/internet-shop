package ru.bakht.internetshop.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.bakht.internetshop.model.dto.MillimeterDto;
import ru.bakht.internetshop.service.BaseProductFeatureService;

@RestController
@RequestMapping("/cameras/millimeters")
public class MillimeterController extends AbstractProductFeatureController<MillimeterDto> {

    public MillimeterController(BaseProductFeatureService<MillimeterDto> service) {
        super(service);
    }
}