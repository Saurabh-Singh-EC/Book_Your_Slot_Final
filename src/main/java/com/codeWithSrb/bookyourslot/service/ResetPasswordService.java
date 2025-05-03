package com.codeWithSrb.bookyourslot.service;

import com.codeWithSrb.bookyourslot.model.ResetPassword;
import com.codeWithSrb.bookyourslot.repository.ResetPasswordRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Optional;

import static com.codeWithSrb.bookyourslot.enumeration.VerificationType.PASSWORD;

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

    public void saveNewResetPasswordLink(ResetPassword resetPassword) {
        resetPasswordRepository.save(resetPassword);
    }

    public boolean isLinkExpired(String key) {
        return resetPasswordRepository.getResetPasswordByResetUrlAndDateIsBeforeCurrentData(getVerificationUrl(key, PASSWORD.getType())) <= 0;
    }

    public Optional<ResetPassword> getUserInfoByResetPasswordUrl(String key) {
        return resetPasswordRepository.getUserInfoByResetPasswordUrl(getVerificationUrl(key, PASSWORD.getType()));
    }

    public String getVerificationUrl(String key, String type) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path("api/v1/booking/verify/" + type + "/" + key).toUriString();
    }
}