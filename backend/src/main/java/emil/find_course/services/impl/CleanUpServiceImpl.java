package emil.find_course.services.impl;

import java.time.Instant;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import emil.find_course.repositories.ConfirmEmailOTTRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class CleanUpServiceImpl {
    
private final ConfirmEmailOTTRepository confirmEmailOTTRepository;

@Scheduled(cron="0 0 3 * * 7")
@Transactional
public void cleanupExpiredVerificationTokens() {
        Instant now = Instant.now();
        try {
            int deletedCount = confirmEmailOTTRepository.deleteByExpirationBefore(now);
            if (deletedCount > 0) {
                log.info("Successfully deleted {} expired verification tokens.", deletedCount);
            } else {
                log.info("No expired verification tokens found to delete.");
            }
        } catch (Exception e) {
            log.error("Error during expired verification token cleanup task", e);
        }
    }

}
