package ru.bakht.internetshop.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import ru.bakht.internetshop.auth.model.BaseEntity;
import ru.bakht.internetshop.auth.model.User;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "audit_logs")
public class AuditLog extends BaseEntity {

    @Column(name = "action")
    String action;

    @Column(name = "entity_name")
    String entityName;

    @Column(name = "entity_id")
    UUID entityId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    User user;

    @Column(name = "timestamp")
    Instant timestamp;

    @Column(name = "details")
    String details;
}
