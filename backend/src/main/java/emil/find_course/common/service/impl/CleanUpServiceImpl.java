package emil.find_course.common.service.impl;

import java.time.Instant;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import emil.find_course.auth.email.repository.ConfirmEmailOTTRepository;
import emil.find_course.auth.password.repository.ResetPasswordOTTRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class CleanUpServiceImpl {

    private final ConfirmEmailOTTRepository confirmEmailOTTRepository;
    private final ResetPasswordOTTRepository resetPasswordOTTRepository;

    @Scheduled(cron = "0 0 3 * * 7")
    @Transactional
    public void weaklyCleanUpFunction() {
        Instant now = Instant.now();
        deleteExpiredResetPasswordTokens(now);
        deleteExpiredConfirmEmailTokens(now);
    }

    private void deleteExpiredConfirmEmailTokens(Instant now) {
        try {
            int deletedCount = confirmEmailOTTRepository.deleteByExpirationBefore(now);
            if (deletedCount > 0) {
                log.info("Successfully deleted {} expired verification tokens.", deletedCount);
            } else {
                log.info("No expired verification tokens found to delete.");
            }
        } catch (Exception e) {
            log.error("Error during deleting expired confirm email tokens cleanup task", e);
        }
    }

    private void deleteExpiredResetPasswordTokens(Instant now) {
        try {
            int deletedCount = resetPasswordOTTRepository.deleteByExpirationBefore(now);
            if (deletedCount > 0) {
                log.info("Successfully deleted {} expired reset password tokens.", deletedCount);
            } else {
                log.info("No expired reset password tokens found to delete.");
            }
        } catch (Exception e) {
            log.error("Error during deleting expired password token cleanup task", e);
        }
    }

}
