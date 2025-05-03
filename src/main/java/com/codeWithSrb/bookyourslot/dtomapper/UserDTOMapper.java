package com.codeWithSrb.bookyourslot.dtomapper;

import com.codeWithSrb.bookyourslot.model.Role;
import com.codeWithSrb.bookyourslot.model.UserInfo;
import com.codeWithSrb.bookyourslot.dto.UserInfoDTO;
import com.codeWithSrb.bookyourslot.dto.UserInfoRegisterDTO;
import org.springframework.beans.BeanUtils;

public class UserDTOMapper {

    public static UserInfoDTO fromUserInfo(UserInfo userInfo) {
        UserInfoDTO userInfoDTO = new UserInfoDTO();
        BeanUtils.copyProperties(userInfo, userInfoDTO);
        return userInfoDTO;
    }

    public static UserInfoDTO fromUserInfo(UserInfo userInfo, Role role) {
        UserInfoDTO userInfoDTO = new UserInfoDTO();
        BeanUtils.copyProperties(userInfo, userInfoDTO);
        userInfoDTO.setRoleName(role.getName());
        userInfoDTO.setPermissions(role.getPermission());
        return userInfoDTO;
    }

    public static UserInfo fromUserInfoRegisterDTO(UserInfoRegisterDTO userInfoRegisterDTO) {
        UserInfo userInfo = new UserInfo();
        BeanUtils.copyProperties(userInfoRegisterDTO, userInfo);
        return userInfo;
    }

}
