package emil.find_course.IntegrationTests.auth.resetPassword;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import emil.find_course.auth.resetPassword.entity.ResetPasswordOTT;
import emil.find_course.auth.resetPassword.repository.ResetPasswordOTTRepository;
import emil.find_course.common.util.TokenGenerator;
import emil.find_course.user.entity.User;

@Component
public class PrepareResetPasswordOTT {

    @Autowired
    private ResetPasswordOTTRepository resetPasswordOTTRepository;

    // Factory
    public ResetPasswordOTT createResetPasswordOTT(User user) {
        String newConfirmEmailToken = TokenGenerator.generateToken6NumCharToken();
        Instant newExpiration = Instant.now().plusSeconds(60 * 60);
        return ResetPasswordOTT.builder().user(user).token(newConfirmEmailToken)
                .expiration(newExpiration).build();

    }

    public ResetPasswordOTT createExpiredResetPasswordOTT(User user) {
        String newConfirmEmailToken = TokenGenerator.generateToken6NumCharToken();
        Instant newExpiration = Instant.now().plusSeconds(0);
        return ResetPasswordOTT.builder().user(user).token(newConfirmEmailToken)
                .expiration(newExpiration).build();
    }

    // PREPARED
    public ResetPasswordOTT prepareResetPasswordOTT(User user) {
        resetPasswordOTTRepository.save(createResetPasswordOTT(user));
        Optional<ResetPasswordOTT> savedOTT = resetPasswordOTTRepository.findByUser(user);
        assertThat(savedOTT).isPresent();
        return savedOTT.get();
    }

    public ResetPasswordOTT prepareExpiredResetPasswordOTT(User user) {
        resetPasswordOTTRepository.save(createExpiredResetPasswordOTT(user));
        Optional<ResetPasswordOTT> savedOTT = resetPasswordOTTRepository.findByUser(user);
        assertThat(savedOTT).isPresent();
        return savedOTT.get();
    }

}
