package com.codeWithSrb.BookYourSlot.Service;

import com.codeWithSrb.BookYourSlot.Model.UserRole;
import com.codeWithSrb.BookYourSlot.Repository.UserRoleRepository;
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

    public void deleteUserRoleById(String id) {
        userRoleRepository.deleteById(id);
    }
}
