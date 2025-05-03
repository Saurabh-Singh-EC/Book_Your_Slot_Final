package com.codeWithSrb.bookyourslot.service;

import com.codeWithSrb.bookyourslot.exception.ApiException;
import com.codeWithSrb.bookyourslot.model.ResetPassword;
import com.codeWithSrb.bookyourslot.model.Role;
import com.codeWithSrb.bookyourslot.model.UserInfo;
import com.codeWithSrb.bookyourslot.model.UserRole;
import com.codeWithSrb.bookyourslot.repository.UserRepository;
import com.codeWithSrb.bookyourslot.dto.ResetLoggedInUserPasswordRequestDTO;
import com.codeWithSrb.bookyourslot.dto.ResetNotLoggedInUserPasswordDTO;
import com.codeWithSrb.bookyourslot.dto.UserInfoDTO;
import com.codeWithSrb.bookyourslot.dto.UserInfoRegisterDTO;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static com.codeWithSrb.bookyourslot.enumeration.RoleName.USER;
import static com.codeWithSrb.bookyourslot.enumeration.VerificationType.PASSWORD;
import static com.codeWithSrb.bookyourslot.dtomapper.UserDTOMapper.fromUserInfo;
import static com.codeWithSrb.bookyourslot.dtomapper.UserDTOMapper.fromUserInfoRegisterDTO;

@Service
@Slf4j
public class UserInfoService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;
    private final UserRoleService userRoleService;
    private final ResetPasswordService resetPasswordService;

    private static final String DATE_TIME_FORMAT = "yyy-MM-dd HH:mm:ss";
    private static final Long PASSWORD_RESET_URL_VALIDITY = 30L;
    private static final String PASSWORD_RESET_LINK_EXPIRED_MESSAGE = "Password reset link is expired. Please reset your password again.";
    private static final String GENERIC_ERROR_MESSAGE = "Something went wrong. Please try again.";
    private static final String NEW_PASSWORD_MISMATCH_ERROR_MESSAGE = "The new password and confirmation password do not match. Please try again.";
    private static final String EMAIL_ALREADY_USED_ERROR_MESSAGE = "Email already used. Please use a new email and try again.";
    private static final String USER_REGISTRATION_ERROR_MESSAGE = "An error occurred during user registration. Please try again.";
    private static final String ROLE_NOT_FOUND_ERROR_MESSAGE = "Role not found";
    private static final String OLD_PASSWORD_MISMATCH_ERROR_MESSAGE = "The old password is incorrect. Please try again.";
    private static final String UNABLE_TO_RESET_PASSWORD = "Unable to reset password. Please try again.";

    public UserInfoService(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           RoleService roleService, UserRoleService userRoleService,
                           ResetPasswordService resetPasswordService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleService = roleService;
        this.userRoleService = userRoleService;
        this.resetPasswordService = resetPasswordService;
    }

    @Transactional
    public UserInfoDTO registerNewUser(UserInfoRegisterDTO userInfoRegisterDTO) {
        validateEmailAvailability(userInfoRegisterDTO.getEmail());
        UserInfo userInfo = prepareUserInfo(userInfoRegisterDTO);

        try {
            Role roleByName = roleService.getRoleByName(USER.name())
                    .orElseThrow(() -> new ApiException(ROLE_NOT_FOUND_ERROR_MESSAGE));

            UserRole userRole = new UserRole(userInfo, roleByName);
            UserRole userRoleResponse = userRoleService.saveNewUserRole(userRole);

            return fromUserInfo(userRoleResponse.getUserInfo(), userRoleResponse.getRole());
        } catch (Exception e) {
            log.error("error: " + e.getMessage());
            throw new ApiException(USER_REGISTRATION_ERROR_MESSAGE);
        }
    }

    private void validateEmailAvailability(String email) {
        if (findUserByEmail(email).isPresent()) {
            throw new ApiException(EMAIL_ALREADY_USED_ERROR_MESSAGE);
        }
    }

    private UserInfo prepareUserInfo(UserInfoRegisterDTO userInfoRegisterDTO) {
        UserInfo userInfo = fromUserInfoRegisterDTO(userInfoRegisterDTO);
        userInfo.setPassword(passwordEncoder.encode(userInfo.getPassword()));
        return userInfo;
    }

    public Optional<UserInfo> findUserByEmail(String email) {
        return userRepository.findUserInfoByEmail(email);
    }

    public void generateResetPasswordLink(UserInfo userInfo) {

        resetPasswordService.deleteExistingResetPasswordLink(userInfo.getEmail());

        String verificationUrl = resetPasswordService.getVerificationUrl(UUID.randomUUID().toString(), PASSWORD.getType());
        log.info(verificationUrl);
        LocalDateTime expiryDate = LocalDateTime.now().plusMinutes(PASSWORD_RESET_URL_VALIDITY);

        ResetPassword resetPassword = new ResetPassword(userInfo.getEmail(), verificationUrl, expiryDate);

        resetPasswordService.saveNewResetPasswordLink(resetPassword);
    }

    public void renewNotLoggedInUserPassword(ResetNotLoggedInUserPasswordDTO resetNotLoggedInUserPasswordDTO) {
        validateNewPassword(resetNotLoggedInUserPasswordDTO.getNewPassword(), resetNotLoggedInUserPasswordDTO.getConfirmNewPassword());

        Optional<ResetPassword> resetPasswordInfoOptional = resetPasswordService.getUserInfoByResetPasswordUrl(resetNotLoggedInUserPasswordDTO.getKey());
        ResetPassword resetPassword = resetPasswordInfoOptional.orElseThrow(() -> new ApiException(UNABLE_TO_RESET_PASSWORD));

        UserInfo userInfo = findUserByEmail(resetPassword.getEmail()).orElseThrow(() -> new ApiException(GENERIC_ERROR_MESSAGE));
        updateUserPassword(userInfo, resetNotLoggedInUserPasswordDTO.getNewPassword());

        resetPasswordService.deleteExistingResetPasswordLink(userInfo.getEmail());
    }

    public UserInfo verifyPasswordKey(String key) {
        if(isLinkExpired(key)) throw new ApiException(PASSWORD_RESET_LINK_EXPIRED_MESSAGE);
        Optional<ResetPassword> resetPasswordInfoOptional = resetPasswordService.getUserInfoByResetPasswordUrl(key);
        ResetPassword resetPassword = resetPasswordInfoOptional.orElseThrow(() -> new ApiException(GENERIC_ERROR_MESSAGE));

        Optional<UserInfo> userByEmail = findUserByEmail(resetPassword.getEmail());
        return userByEmail.orElseThrow(() -> new ApiException(GENERIC_ERROR_MESSAGE));
    }

    private boolean isLinkExpired(String key) {
        return resetPasswordService.isLinkExpired(key);
    }

    @Transactional
    public void renewLoggedInUserPassword(ResetLoggedInUserPasswordRequestDTO resetLoggedInUserPasswordRequestDTO, UserInfo userInfo) {

        validateOldPassword(resetLoggedInUserPasswordRequestDTO.getOldPassword(), userInfo.getPassword());

        validateNewPassword(resetLoggedInUserPasswordRequestDTO.getNewPassword(), resetLoggedInUserPasswordRequestDTO.getConfirmNewPassword());

        updateUserPassword(userInfo, resetLoggedInUserPasswordRequestDTO.getNewPassword());

        //TODO: think of how to logout user after successful password change
    }

    private void validateOldPassword(String oldPasswordFromUser, String oldPasswordFromUserProfile) {
        if (!passwordEncoder.matches(oldPasswordFromUser, oldPasswordFromUserProfile))
            throw new ApiException(OLD_PASSWORD_MISMATCH_ERROR_MESSAGE);
    }

    private void validateNewPassword(String newPassword, String confirmNewPassword) {
        if (!newPassword.equals(confirmNewPassword))
            throw new ApiException(NEW_PASSWORD_MISMATCH_ERROR_MESSAGE);
    }

    public void updateUserPassword(UserInfo userInfo, String newPassword) {
        userInfo.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(userInfo);
    }
}