package com.codeWithSrb.bookyourslot.dto;

import lombok.Data;

@Data
public class ResetLoggedInUserPasswordRequestDTO {

    private String oldPassword;
    private String newPassword;
    private String confirmNewPassword;
}
