package emil.find_course.services.impl;

import java.time.Instant;
import java.util.Optional;

import org.springframework.stereotype.Service;

import emil.find_course.domains.entities.ConfirmEmailOTT;
import emil.find_course.domains.entities.user.User;
import emil.find_course.repositories.ConfirmEmailOTTRepository;
import emil.find_course.repositories.UserRepository;
import emil.find_course.services.EmailVerificationService;
import emil.find_course.utils.TokenGenerator;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailVerificationServiceImpl implements EmailVerificationService {

    private final ConfirmEmailOTTRepository confirmEmailOTTRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void validateEmail(User user, String token) {
        if (user.isEmailVerified()) {
            throw new IllegalArgumentException("Email is already verified");
        }
        ConfirmEmailOTT confirmOTT = confirmEmailOTTRepository.findByUser(user)
                .orElseThrow(() -> new EntityNotFoundException("Couldn't find token"));

        if (confirmOTT.getExpiration().isBefore(Instant.now())) {
            confirmEmailOTTRepository.delete(confirmOTT);
            throw new IllegalArgumentException("Token has expired");
        }
        if (!confirmOTT.getToken().equals(token)) {
            throw new IllegalArgumentException("Invalid token");
        }
        user.setEmailVerified(true);
        userRepository.save(user);
        confirmEmailOTTRepository.delete(confirmOTT);

    }

    @Override
    @Transactional
    public void resendConfirmEmail(User user) {
        if (user.isEmailVerified()) {
            throw new IllegalArgumentException("Email is already verified");
        }
        Optional<ConfirmEmailOTT> oldConfirmEmailOTT = confirmEmailOTTRepository.findByUser(user);

        if (oldConfirmEmailOTT.isPresent()) {
            confirmEmailOTTRepository.delete(oldConfirmEmailOTT.get());
        }

        String confirmEmailToken = generateConfirmEmailToken(user);
    }

    @Override
    @Transactional
    public String generateConfirmEmailToken(User user) {
        Optional<ConfirmEmailOTT> confirmOTT = confirmEmailOTTRepository.findByUser(user);
        if (confirmOTT.isPresent()) {
            confirmEmailOTTRepository.delete(confirmOTT.get());
        }

        String confirmEmailToken = TokenGenerator.generateToken6NumCharToken();

        ConfirmEmailOTT confirmEmailOTT = ConfirmEmailOTT.builder().user(user).token(confirmEmailToken)
                .expiration(Instant.now().plusSeconds(60 * 15)).build();

        confirmEmailOTTRepository.save(confirmEmailOTT);
        return confirmEmailToken;
    }

}
