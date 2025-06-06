package emil.find_course.IntegrationTests.auth.confirmEmail;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.Optional;

import org.springframework.stereotype.Component;

import emil.find_course.auth.confirmEmail.entity.ConfirmEmailOTT;
import emil.find_course.auth.confirmEmail.repository.ConfirmEmailOTTRepository;
import emil.find_course.common.util.TokenGenerator;
import emil.find_course.user.entity.User;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PrepareConfirmEmailOTT {
    private final ConfirmEmailOTTRepository confirmEmailOTTRepository;

    // Factory
    public ConfirmEmailOTT createConfirmEmailOTT(User user) {
        String newConfirmEmailToken = TokenGenerator.generateToken6NumCharToken();
        Instant newExpiration = Instant.now().plusSeconds(60 * 15);
        return ConfirmEmailOTT.builder().user(user).token(newConfirmEmailToken)
                .expiration(newExpiration).build();

    }

    public ConfirmEmailOTT createExpiredConfirmEmailOTT(User user) {
        String newConfirmEmailToken = TokenGenerator.generateToken6NumCharToken();
        Instant newExpiration = Instant.now().plusSeconds(0);
        return ConfirmEmailOTT.builder().user(user).token(newConfirmEmailToken)
                .expiration(newExpiration).build();
    }

    // PREPARED
    public ConfirmEmailOTT prepareConfirmEmailOTT(User user) {
        confirmEmailOTTRepository.save(createConfirmEmailOTT(user));
        Optional<ConfirmEmailOTT> savedOTT = confirmEmailOTTRepository.findByUser(user);
        assertThat(savedOTT).isPresent();
        return savedOTT.get();
    }

    public ConfirmEmailOTT prepareExpiredConfirmEmailOTT(User user) {
        confirmEmailOTTRepository.save(createExpiredConfirmEmailOTT(user));
        Optional<ConfirmEmailOTT> savedOTT = confirmEmailOTTRepository.findByUser(user);
        assertThat(savedOTT).isPresent();
        return savedOTT.get();
    }

}
