package ru.bakht.internetshop.model.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.bakht.internetshop.auth.model.dto.UserInfoDto;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderFilterDto {

    UserInfoDto user;
    Instant startDate;
    Instant endDate;
}
