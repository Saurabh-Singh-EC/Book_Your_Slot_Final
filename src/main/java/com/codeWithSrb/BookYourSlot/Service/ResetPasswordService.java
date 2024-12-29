package com.codeWithSrb.BookYourSlot.Service;

import com.codeWithSrb.BookYourSlot.Model.ResetPassword;
import com.codeWithSrb.BookYourSlot.Repository.ResetPasswordRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import static com.codeWithSrb.BookYourSlot.Enumeration.VerificationType.PASSWORD;

@Service
@Slf4j
public class ResetPasswordService {

    private final ResetPasswordRepository resetPasswordRepository;

    public ResetPasswordService(ResetPasswordRepository resetPasswordRepository) {
        this.resetPasswordRepository = resetPasswordRepository;
    }

    @Transactional
    public void deleteExistingResetPasswordLink(String email) {
        resetPasswordRepository.deleteResetPasswordByEmail(email);
    }

    @Transactional
    public void deleteResetPasswordLinkByResetUrl(String key) {
        resetPasswordRepository.deleteResetPasswordLinkByResetUrl(getVerificationUrl(key, PASSWORD.getType()));
    }

    public void saveNewResetPasswordLink(ResetPassword resetPassword) {
        resetPasswordRepository.save(resetPassword);
    }

    public boolean isLinkExpired(String key) {
        return resetPasswordRepository.getResetPasswordByResetUrlAndDateIsBeforeCurrentData(getVerificationUrl(key, PASSWORD.getType())) <= 0;
    }

    public String getUserInfoByResetPasswordUrl(String key) {
        return resetPasswordRepository.getUserInfoByResetPasswordUrl(getVerificationUrl(key, PASSWORD.getType()));
    }

    public String getVerificationUrl(String key, String type) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path("api/v1/booking/verify/" + type + "/" + key).toUriString();
    }
}