package com.codeWithSrb.BookYourSlot.config;

import com.codeWithSrb.BookYourSlot.Model.Role;
import com.codeWithSrb.BookYourSlot.Model.UserInfo;
import com.codeWithSrb.BookYourSlot.dto.UserInfoDTO;
import com.codeWithSrb.BookYourSlot.dtomapper.UserDTOMapper;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

public class UserDetailsImpl implements UserDetails {

    private final UserInfo userInfo;
    private final Role role;

    public UserDetailsImpl(UserInfo userInfo, Role role) {
        this.userInfo = userInfo;
        this.role = role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return stream(role.getPermission().split(",".trim())).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return userInfo.getPassword();
    }

    @Override
    public String getUsername() {
        return userInfo.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public UserInfoDTO getUser() {
        return UserDTOMapper.fromUserInfo(this.userInfo, role);
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public Role getRole() {
        return role;
    }
}