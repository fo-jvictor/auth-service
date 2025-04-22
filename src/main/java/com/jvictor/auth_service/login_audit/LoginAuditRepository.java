package com.jvictor.auth_service.login_audit;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public interface LoginAuditRepository extends JpaRepository<LoginAudit, String> {
}
