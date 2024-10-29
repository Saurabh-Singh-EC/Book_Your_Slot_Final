package com.codeWithSrb.BookYourSlot.Service;

import com.codeWithSrb.BookYourSlot.Enumeration.RoleName;
import com.codeWithSrb.BookYourSlot.Exception.ApiException;
import com.codeWithSrb.BookYourSlot.Model.Role;
import com.codeWithSrb.BookYourSlot.Model.UserInfo;
import com.codeWithSrb.BookYourSlot.Model.UserRole;
import com.codeWithSrb.BookYourSlot.Repository.UserRepository;
import com.codeWithSrb.BookYourSlot.dto.UserInfoDTO;
import com.codeWithSrb.BookYourSlot.dto.UserInfoRegisterDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.codeWithSrb.BookYourSlot.dtomapper.UserDTOMapper.fromUserInfo;
import static com.codeWithSrb.BookYourSlot.dtomapper.UserDTOMapper.fromUserInfoRegisterDTO;

@Service
@Slf4j
public class UserInfoService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;
    private final UserRoleService userRoleService;

    public UserInfoService(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           RoleService roleService, UserRoleService userRoleService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleService = roleService;
        this.userRoleService = userRoleService;
    }

    public UserInfoDTO registerNewUser(UserInfoRegisterDTO userInfoRegisterDTO) {
        validateEmailAvailability(userInfoRegisterDTO.getEmail());
        UserInfo userInfo = prepareUserInfo(userInfoRegisterDTO);

        try {
            Role roleByName = roleService.getRoleByName(RoleName.ROLE_USER.name())
                    .orElseThrow(() -> new ApiException("Role not found"));

            UserRole userRole = new UserRole(userInfo, roleByName);
            UserRole userRoleResponse = userRoleService.saveNewUserRole(userRole);

            return fromUserInfo(userRoleResponse.getUserInfo(), userRoleResponse.getRole());
        } catch (Exception e) {
            log.error("error: " + e.getMessage());
            throw new ApiException("An error occurred during user registration. Please try again.");
        }
    }

    private void validateEmailAvailability(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new ApiException("Email already used. Please use a new email and try again.");
        }
    }

    private UserInfo prepareUserInfo(UserInfoRegisterDTO userInfoRegisterDTO) {
        UserInfo userInfo = fromUserInfoRegisterDTO(userInfoRegisterDTO);
        userInfo.setPassword(passwordEncoder.encode(userInfo.getPassword()));
        return userInfo;
    }

    public UserInfo findUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new ApiException("An error occurred. Please try again"));
    }
}
