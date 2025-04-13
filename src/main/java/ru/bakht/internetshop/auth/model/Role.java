package ru.bakht.internetshop.auth.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import ru.bakht.internetshop.auth.model.enums.RoleName;

@Getter
@Setter
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "roles")
public class Role extends TimestampedEntity {

    @Enumerated(EnumType.STRING)
    RoleName name;
}
