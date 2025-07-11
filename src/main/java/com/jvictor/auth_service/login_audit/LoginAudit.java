package com.jvictor.auth_service.login_audit;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "login_audit")
public class LoginAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String username;
    private String hashedIdAddress;
    private String userAgent;
    private Boolean success;
    private String reason;
    private LocalDateTime timestamp;

    public LoginAudit(String username,
                      String hashedIdAddress,
                      String userAgent,
                      Boolean success,
                      String reason,
                      LocalDateTime timestamp) {
        this.username = username;
        this.hashedIdAddress = hashedIdAddress;
        this.userAgent = userAgent;
        this.success = success;
        this.reason = reason;
        this.timestamp = timestamp;
    }
}
