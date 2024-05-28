package com.codeWithSrb.BookYourSlot.Repository;

import com.codeWithSrb.BookYourSlot.Model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRoleRepository extends JpaRepository<UserRole, String> {
}
