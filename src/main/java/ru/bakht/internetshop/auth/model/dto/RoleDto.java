package ru.bakht.internetshop.auth.model.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import ru.bakht.internetshop.auth.model.enums.RoleName;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoleDto extends TimestampedDto {

    RoleName name;
}
