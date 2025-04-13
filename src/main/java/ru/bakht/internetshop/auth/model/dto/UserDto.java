package ru.bakht.internetshop.auth.model.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDto extends TimestampedDto {

    String email;
    boolean isTwoFactorEnabled;
    UserInfoDto userInfo;
}
