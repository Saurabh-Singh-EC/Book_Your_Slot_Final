package com.codeWithSrb.BookYourSlot.Model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "reset_password")
public class ResetPassword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String resetUrl;
    private String date;

    public ResetPassword() {
    }

    public ResetPassword(String email, String resetUrl, String date) {
        this.email = email;
        this.resetUrl = resetUrl;
        this.date = date;
    }
}