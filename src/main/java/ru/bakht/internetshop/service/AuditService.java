package ru.bakht.internetshop.service;

import java.util.UUID;

public interface AuditService {

    void logAction(String action, String entityName, UUID entityId, String details);
}
