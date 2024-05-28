package com.codeWithSrb.BookYourSlot.Repository;

import com.codeWithSrb.BookYourSlot.Model.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserInfo, Integer> {

    UserInfo findByEmail(String email);
}
