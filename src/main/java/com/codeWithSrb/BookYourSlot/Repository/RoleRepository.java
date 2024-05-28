package com.codeWithSrb.BookYourSlot.Repository;

import com.codeWithSrb.BookYourSlot.Model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RoleRepository extends JpaRepository<Role, Integer> {

    Role findByName(String name);

    @Query(nativeQuery = true, value = "select r.id, r.name, r.permission from BookYourSlot.role r join BookYourSlot.user_role ur on ur.fk_role_id = r.id join BookYourSlot.user_info ui on ui.id = ur.fk_user_id where ui.id = :id")
    Role findRoleByUserId(@Param("id") int id);
}
