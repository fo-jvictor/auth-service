package com.jvictor.auth_service.blocked_ip;

import com.jvictor.auth_service.utils.HashService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BlockedIpService {
    private final BlockedIpRepository blockedIpRepository;
    private final HashService hashService;

    public BlockedIp saveBlockedIp(String ipAddress) {
        System.out.println("Saving blocked ip: " + ipAddress);
        BlockedIp blockedIp = getBlockedIpByIp(ipAddress);
        blockedIp.setBlockCount(blockedIp.getBlockCount() + 1);
        blockedIp.setLastTimeBlockedAt(LocalDateTime.now());
        return blockedIpRepository.save(blockedIp);
    }

    public Boolean isIpBlocked(String ipAddress) {
        String hashedIpAddress = hashService.hashRawValue(ipAddress);
        return blockedIpRepository.findByHashedIpAddress(hashedIpAddress)
                .filter(ip -> ip.getBlockedUntil().isAfter(LocalDateTime.now()))
                .isPresent();
    }

    private BlockedIp getBlockedIpByIp(String ipAddress) {
        String hashedIpAddress = hashService.hashRawValue(ipAddress);
        return blockedIpRepository.findByHashedIpAddress(hashedIpAddress)
                .orElseGet(() -> buildBlockedIpEntity(hashedIpAddress));
    }

    private BlockedIp buildBlockedIpEntity(String blockedIp) {
        return BlockedIp.builder()
                .hashedIpAddress(blockedIp)
                .blockCount(1L)
                .blockedUntil(LocalDateTime.now().plusHours(12))
                .build();
    }
}
