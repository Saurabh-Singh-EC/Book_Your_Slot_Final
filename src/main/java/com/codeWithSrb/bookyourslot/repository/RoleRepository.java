package com.codeWithSrb.bookyourslot.repository;

import com.codeWithSrb.bookyourslot.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {

    Optional<Role> findByName(String name);

    @Query("SELECT r FROM Role r " +
            "join UserRole ur " +
            "on ur.role.id = r.id " +
            "join UserInfo ui " +
            "on ui.id = ur.userInfo.id " +
            "where ui.id = :id")
    Role findRoleByUserId(@Param("id") int id);
}
