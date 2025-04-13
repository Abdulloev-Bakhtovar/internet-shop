package ru.bakht.internetshop.auth.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;


import java.time.Instant;

@Getter
@Setter
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "tokens")
public class Token extends BaseEntity {

    String token;
    Instant createdDate;
    Instant expiresDate;

    @ManyToOne
    @JoinColumn(name = "token_type_id")
    TokenType tokenType;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;
}
