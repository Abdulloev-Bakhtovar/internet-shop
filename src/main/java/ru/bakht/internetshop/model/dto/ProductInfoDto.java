package ru.bakht.internetshop.model.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import ru.bakht.internetshop.auth.model.dto.BaseDto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductInfoDto extends BaseDto {

    String name;
    Instant createDate;
    Instant updateDate;
    BigDecimal price;
    BigDecimal discount;
    Boolean isInterestDiscount;
    String description;
    MillimeterDto millimeter;
    MegapixelDto megapixel;
    TypeDto type;
    List<ImageDto> images;
}