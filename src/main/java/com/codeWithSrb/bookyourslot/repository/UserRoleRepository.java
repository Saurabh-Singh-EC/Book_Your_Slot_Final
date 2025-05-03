package com.codeWithSrb.bookyourslot.repository;

import com.codeWithSrb.bookyourslot.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRoleRepository extends JpaRepository<UserRole, String> {
}
