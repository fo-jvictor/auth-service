package com.jvictor.auth_service.blocked_ip;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
public class BlockedIp {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String ipAddress;
    private Long blockCount;
    private LocalDateTime lastTimeBlockedAt;
    private LocalDateTime blockedUntil;
}
