package ru.bakht.internetshop.auth.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import ru.bakht.internetshop.auth.model.enums.ChangeType;

@Getter
@Setter
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "login_info_changes")
public class LoginInfoChange extends TimestampedEntity {

    @Enumerated(EnumType.STRING)
    ChangeType changeType;
    String value;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    User user;
}
