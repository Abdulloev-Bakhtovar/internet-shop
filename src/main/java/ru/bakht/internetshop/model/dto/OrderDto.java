package ru.bakht.internetshop.model.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import ru.bakht.internetshop.auth.model.dto.BaseDto;
import ru.bakht.internetshop.auth.model.dto.UserInfoDto;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderDto extends BaseDto {

    Instant orderDate;
    String description;
    UserInfoDto userInfo;
    List<ProductDto> products;
}
