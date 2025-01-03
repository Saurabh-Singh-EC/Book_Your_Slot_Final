package com.codeWithSrb.BookYourSlot.dto;

import lombok.Data;

@Data
public class ResetNotLoggedInUserPasswordDTO {

    private String key;
    private String newPassword;
    private String confirmNewPassword;
}
