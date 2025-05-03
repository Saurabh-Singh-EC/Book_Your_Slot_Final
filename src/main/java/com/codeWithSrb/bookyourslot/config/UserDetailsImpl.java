package com.codeWithSrb.bookyourslot.config;

import com.codeWithSrb.bookyourslot.model.Role;
import com.codeWithSrb.bookyourslot.model.UserInfo;
import com.codeWithSrb.bookyourslot.dto.UserInfoDTO;
import com.codeWithSrb.bookyourslot.dtomapper.UserDTOMapper;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

@Getter
public class UserDetailsImpl implements UserDetails {

    private final UserInfo userInfo;
    private final Role role;

    public UserDetailsImpl(UserInfo userInfo, Role role) {
        this.userInfo = userInfo;
        this.role = role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorities = stream(role.getPermission().split(",")).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
        authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));

        return authorities;
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
        return UserDTOMapper.fromUserInfo(this.userInfo, this.role);
    }
}