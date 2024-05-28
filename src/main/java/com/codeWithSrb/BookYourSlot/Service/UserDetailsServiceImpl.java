package com.codeWithSrb.BookYourSlot.Service;

import com.codeWithSrb.BookYourSlot.Exception.ApiException;
import com.codeWithSrb.BookYourSlot.Model.Role;
import com.codeWithSrb.BookYourSlot.Model.UserInfo;
import com.codeWithSrb.BookYourSlot.Model.UserRole;
import com.codeWithSrb.BookYourSlot.Repository.UserRepository;
import com.codeWithSrb.BookYourSlot.config.UserDetailsImpl;
import com.codeWithSrb.BookYourSlot.dto.UserInfoDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static com.codeWithSrb.BookYourSlot.Enumeration.RoleType.ROLE_USER;
import static com.codeWithSrb.BookYourSlot.dtomapper.UserDTOMapper.fromUserInfo;

@Service
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleService roleService;
    @Autowired
    private UserRoleService userRoleService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserInfo userInfo = findUserByEmail(email);

        if(userInfo == null) {
            log.error("User not found in the database");
            throw new UsernameNotFoundException("User not found in the database");
        } else {
            log.info("User found in the database {}", email);
            return new UserDetailsImpl(userInfo, findRoleByUserId(userInfo.getId()));
        }
    }

    private Role findRoleByUserId(int id) {
        log.info("checking role for user id : {}", id);
        try {
            log.info(roleService.getRoleByUserId(id).getPermission());
            return roleService.getRoleByUserId(id);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ApiException("An error occurred. Please try again.");
        }
    }

    public UserInfo findUserByEmail(String email) {
        try{
            return userRepository.findByEmail(email);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ApiException("An error occurred. Please try again");
        }
    }

    public UserInfoDTO registerNewUser(UserInfo userInfo) {
        if(isEmailAlreadyExists(userInfo.getEmail())) throw new ApiException("Email already used. Please use new email and try again.");
        userInfo.setPassword(passwordEncoder.encode(userInfo.getPassword()));
        try {
            UserInfo userResponse = userRepository.save(userInfo);
            Role roleResponse = roleService.getRoleByName(ROLE_USER.name());
            userRoleService.saveNewUserRole(new UserRole(userResponse, roleResponse));
            return fromUserInfo(userResponse);
        } catch (Exception e) {
            log.error("error: " + e.getMessage());
            throw new ApiException("An error occurred while account creation. Please try again.");
        }
    }

    private boolean isEmailAlreadyExists(String email) {
        UserInfo userInfo = userRepository.findByEmail(email);
        return Objects.nonNull(userInfo);
    }
}
