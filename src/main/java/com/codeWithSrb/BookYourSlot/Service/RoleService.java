package com.codeWithSrb.BookYourSlot.Service;

import com.codeWithSrb.BookYourSlot.Enumeration.RoleName;
import com.codeWithSrb.BookYourSlot.Enumeration.RolePermission;
import com.codeWithSrb.BookYourSlot.Model.Role;
import com.codeWithSrb.BookYourSlot.Repository.RoleRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @PostConstruct
    public void initRoles() {
        if (roleRepository.findByName(RoleName.ROLE_ADMIN.name()).isEmpty()) {
            roleRepository.save(new Role(RoleName.ROLE_ADMIN.name(), RolePermission.ADMIN.getPermission()));
        }
        if (roleRepository.findByName(RoleName.ROLE_USER.name()).isEmpty()) {
            roleRepository.save(new Role(RoleName.ROLE_USER.name(), RolePermission.USER.getPermission()));
        }
    }


    public Role getRoleByUserId(int id) {
        return roleRepository.findRoleByUserId(id);
    }

    public Optional<Role> getRoleByName(String name) {
        return roleRepository.findByName(name);
    }
}