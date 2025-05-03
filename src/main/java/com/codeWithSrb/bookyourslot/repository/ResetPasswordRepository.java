package com.codeWithSrb.bookyourslot.repository;

import com.codeWithSrb.bookyourslot.model.ResetPassword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResetPasswordRepository extends JpaRepository<ResetPassword, Long> {

    @Modifying
    @Query("Delete from ResetPassword rp where rp.email= :email")
    void deleteResetPasswordByEmail(@Param("email") String email);

    @Query("select count(rp) from ResetPassword rp where rp.resetUrl= :resetUrl and rp.date >= CURRENT_TIMESTAMP")
    int getResetPasswordByResetUrlAndDateIsBeforeCurrentData(@Param("resetUrl") String resetUrl);

    @Query("select rp from ResetPassword rp where rp.resetUrl= :resetUrl")
    Optional<ResetPassword> getUserInfoByResetPasswordUrl(@Param("resetUrl") String resetUrl);
}