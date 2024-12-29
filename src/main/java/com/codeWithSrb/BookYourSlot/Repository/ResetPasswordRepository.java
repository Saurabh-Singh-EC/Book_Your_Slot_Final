package com.codeWithSrb.BookYourSlot.Repository;

import com.codeWithSrb.BookYourSlot.Model.ResetPassword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ResetPasswordRepository extends JpaRepository<ResetPassword, Long> {

    @Modifying
    @Query(nativeQuery = true, value = "Delete from BookYourSlot.reset_password rp where rp.email= :email")
    void deleteResetPasswordByEmail(String email);

    @Query(nativeQuery = true, value = "select count(*) from BookYourSlot.reset_password rp where rp.reset_url= :resetUrl and rp.date >= CURRENT_TIMESTAMP")
    int getResetPasswordByResetUrlAndDateIsBeforeCurrentData(String resetUrl);

    @Query(nativeQuery = true, value = "select rp.email from BookYourSlot.reset_password rp where rp.reset_url= :resetUrl")
    String getUserInfoByResetPasswordUrl(String resetUrl);

    @Modifying
    @Query(nativeQuery = true, value = "Delete from BookYourSlot.reset_password rp where rp.reset_url= :resetUrl")
    void deleteResetPasswordLinkByResetUrl(String resetUrl);
}
