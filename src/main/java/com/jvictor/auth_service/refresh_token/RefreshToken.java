package com.jvictor.auth_service.refresh_token;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@Entity
@Table(name = "refresh_token")
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String token;
    private String userId;
    private String salt;
    private Boolean isRevoked;
    private LocalDateTime expiryDate;
}
