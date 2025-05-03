package com.codeWithSrb.bookyourslot.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "reset_password")
public class ResetPassword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String resetUrl;
    private LocalDateTime date;

    public ResetPassword() {
    }

    public ResetPassword(String email, String resetUrl, LocalDateTime date) {
        this.email = email;
        this.resetUrl = resetUrl;
        this.date = date;
    }
}