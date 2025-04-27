package com.jvictor.auth_service.blocked_ip;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BlockedIpService {
    private final BlockedIpRepository blockedIpRepository;

    public BlockedIp saveBlockedIp(String ipAddress) {
        System.out.println("Saving blocked ip: " + ipAddress);
        BlockedIp blockedIp = getBlockedIpByIp(ipAddress);
        blockedIp.setBlockCount(blockedIp.getBlockCount() + 1);
        blockedIp.setLastTimeBlockedAt(LocalDateTime.now());
        return blockedIpRepository.save(blockedIp);
    }

    public Boolean isIpBlocked(String ipAddress) {
        return blockedIpRepository.findByIpAddress(ipAddress)
                .filter(ip -> ip.getBlockedUntil().isAfter(LocalDateTime.now()))
                .isPresent();
    }

    private BlockedIp getBlockedIpByIp(String ipAddress) {
        return blockedIpRepository.findByIpAddress(ipAddress)
                .orElseGet(() -> buildBlockedIpEntity(ipAddress));
    }

    private BlockedIp buildBlockedIpEntity(String blockedIp) {
        return BlockedIp.builder()
                .ipAddress(blockedIp)
                .blockCount(1L)
                .blockedUntil(LocalDateTime.now().plusHours(12))
                .build();
    }
}
