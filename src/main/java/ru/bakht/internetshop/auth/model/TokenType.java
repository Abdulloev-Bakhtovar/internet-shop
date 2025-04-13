package ru.bakht.internetshop.auth.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import ru.bakht.internetshop.auth.model.enums.TokenTypeName;

@Getter
@Setter
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "token_types")
public class TokenType extends BaseEntity {

    @Enumerated(EnumType.STRING)
    TokenTypeName name;
}
