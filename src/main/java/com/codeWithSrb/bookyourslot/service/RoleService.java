package com.codeWithSrb.bookyourslot.service;

import com.codeWithSrb.bookyourslot.model.Role;
import com.codeWithSrb.bookyourslot.repository.RoleRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.codeWithSrb.bookyourslot.enumeration.RoleName.ADMIN;
import static com.codeWithSrb.bookyourslot.enumeration.RoleName.USER;
import static com.codeWithSrb.bookyourslot.enumeration.RolePermission.ROLE_ADMIN;
import static com.codeWithSrb.bookyourslot.enumeration.RolePermission.ROLE_USER;

@Service
@Slf4j
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @PostConstruct
    public void initRoles() {
        if (roleRepository.findByName(ADMIN.name()).isEmpty()) {
            roleRepository.save(new Role(ADMIN.name(), ROLE_ADMIN.getPermission()));
        }
        if (roleRepository.findByName(USER.name()).isEmpty()) {
            roleRepository.save(new Role(USER.name(), ROLE_USER.getPermission()));
        }
    }


    public Role getRoleByUserId(int id) {
        return roleRepository.findRoleByUserId(id);
    }

    public Optional<Role> getRoleByName(String name) {
        return roleRepository.findByName(name);
    }
}