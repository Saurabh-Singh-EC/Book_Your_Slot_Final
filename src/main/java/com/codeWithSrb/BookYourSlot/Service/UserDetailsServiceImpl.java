package com.codeWithSrb.BookYourSlot.Service;

import com.codeWithSrb.BookYourSlot.Model.UserInfo;
import com.codeWithSrb.BookYourSlot.config.UserDetailsImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    private RoleService roleService;
    private UserInfoService userInfoService;

    public UserDetailsServiceImpl(RoleService roleService, UserInfoService userInfoService) {
        this.roleService = roleService;
        this.userInfoService = userInfoService;
    }

    public UserDetailsServiceImpl() {
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserInfo userInfo = userInfoService.findUserByEmail(email);

        if(userInfo == null) {
            log.error("User not found in the database");
            throw new UsernameNotFoundException("User not found in the database");
        } else {
            log.info("User found in the database {}", email);
            return new UserDetailsImpl(userInfo, roleService.getRoleByUserId(userInfo.getId()));
        }
    }
}