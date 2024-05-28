package com.codeWithSrb.BookYourSlot.Service;

import com.codeWithSrb.BookYourSlot.Model.UserRole;
import com.codeWithSrb.BookYourSlot.Repository.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserRoleService {

    @Autowired
    private UserRoleRepository userRoleRepository;

    public void saveNewUserRole(UserRole userRole) {
        userRoleRepository.save(userRole);
    }
}
