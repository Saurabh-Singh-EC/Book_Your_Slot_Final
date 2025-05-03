package com.codeWithSrb.bookyourslot.dto;

import lombok.Data;

@Data
public class ResetNotLoggedInUserPasswordDTO {

    private String key;
    private String newPassword;
    private String confirmNewPassword;
}
