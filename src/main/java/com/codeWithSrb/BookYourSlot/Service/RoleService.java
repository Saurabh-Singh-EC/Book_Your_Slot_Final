package com.codeWithSrb.BookYourSlot.Service;

import com.codeWithSrb.BookYourSlot.Exception.ApiException;
import com.codeWithSrb.BookYourSlot.Model.Role;
import com.codeWithSrb.BookYourSlot.Repository.RoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;


    public Role getRoleByUserId(int id) {
        try {
            return roleRepository.findRoleByUserId(id);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ApiException("An error occurred. Please try again");
        }
    }

    public Role getRoleByName(String name) {
        try {
            return roleRepository.findByName(name);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ApiException("An error occurred. Please try again");
        }
    }
}
