package ru.bakht.internetshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.bakht.internetshop.model.AuditLog;

public interface AuditLogRepo extends JpaRepository<AuditLog, Long> {

}
