package com.codeWithSrb.BookYourSlot.Service;

import com.codeWithSrb.BookYourSlot.config.UserDetailsImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    private final RoleService roleService;
    private final UserInfoService userInfoService;

    public UserDetailsServiceImpl(RoleService roleService, UserInfoService userInfoService) {
        this.roleService = roleService;
        this.userInfoService = userInfoService;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userInfoService.findUserByEmail(email)
                .map(user -> new UserDetailsImpl(user, roleService.getRoleByUserId(user.getId())))
                .orElseThrow(() -> new UsernameNotFoundException(String.format("User with name : %s does not exist", email)));
    }
}