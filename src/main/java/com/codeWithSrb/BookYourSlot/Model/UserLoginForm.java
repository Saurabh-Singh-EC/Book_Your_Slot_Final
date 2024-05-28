package com.codeWithSrb.BookYourSlot.Model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class UserLoginForm {

    @NotEmpty(message = "Email cannot be null or empty")
    @Email(message = "Invalid email. Please enter a valid email address")
    private String email;

    @NotEmpty(message = "Password cannot be null or empty")
    private String password;

    public UserLoginForm() {
    }

    public UserLoginForm(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
