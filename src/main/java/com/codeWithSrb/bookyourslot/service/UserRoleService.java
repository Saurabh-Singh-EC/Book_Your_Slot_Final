package com.codeWithSrb.bookyourslot.service;

import com.codeWithSrb.bookyourslot.model.UserRole;
import com.codeWithSrb.bookyourslot.repository.UserRoleRepository;
import org.springframework.stereotype.Service;

@Service
public class UserRoleService {

    private final UserRoleRepository userRoleRepository;

    public UserRoleService(UserRoleRepository userRoleRepository) {
        this.userRoleRepository = userRoleRepository;
    }

    public UserRole saveNewUserRole(UserRole userRole) {
        return userRoleRepository.save(userRole);
    }
}
