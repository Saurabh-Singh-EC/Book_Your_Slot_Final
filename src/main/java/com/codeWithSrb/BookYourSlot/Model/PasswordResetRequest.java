package com.codeWithSrb.BookYourSlot.Model;

import lombok.Data;

@Data
public class PasswordResetRequest {

    private String key;
    private String newPassword;
    private String confirmNewPassword;
}
