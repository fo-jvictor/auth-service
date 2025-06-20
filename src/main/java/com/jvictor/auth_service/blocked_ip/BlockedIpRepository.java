package com.jvictor.auth_service.blocked_ip;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BlockedIpRepository extends JpaRepository<BlockedIp, String> {
    Optional<BlockedIp> findByHashedIpAddress(String ipAddress);
}
