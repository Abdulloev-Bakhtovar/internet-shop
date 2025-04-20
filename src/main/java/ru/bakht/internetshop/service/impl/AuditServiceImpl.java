package ru.bakht.internetshop.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bakht.internetshop.auth.util.AuthUtils;
import ru.bakht.internetshop.model.AuditLog;
import ru.bakht.internetshop.repository.AuditLogRepo;
import ru.bakht.internetshop.service.AuditService;

import java.time.Instant;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class AuditServiceImpl implements AuditService {

    @Value("${application.audit.enabled}")
    private boolean auditEnabled;

    private final AuditLogRepo auditLogRepo;
    private final AuthUtils authUtils;

    @Override
    public void logAction(String action, String entityName, UUID entityId, String details) {
        if (!auditEnabled) return;

        var log = AuditLog.builder()
                .action(action)
                .entityName(entityName)
                .entityId(entityId)
                .user(authUtils.getAuthenticatedUser() == null ? null : authUtils.getAuthenticatedUser())
                .timestamp(Instant.now())
                .details(details)
                .build();
        auditLogRepo.save(log);
    }
}