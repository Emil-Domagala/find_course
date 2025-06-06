package emil.find_course.auth.emailVerification;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import emil.find_course.auth.emailVerification.entity.ConfirmEmailOTT;
import emil.find_course.auth.emailVerification.repository.ConfirmEmailOTTRepository;
import emil.find_course.common.service.EmailService;
import emil.find_course.common.util.TokenGenerator;
import emil.find_course.user.entity.User;
import emil.find_course.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailVerificationServiceImpl implements EmailVerificationService {

    private final ConfirmEmailOTTRepository confirmEmailOTTRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

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
    public void sendVerificationEmail(User user) {
        if (user.isEmailVerified()) {
            throw new IllegalArgumentException("Email is already verified");
        }

        String confirmEmailToken = generateConfirmEmailToken(user);

        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("user", user);
        templateModel.put("token", confirmEmailToken);

        emailService.sendHtmlEmail(
                user.getEmail(),
                "Verify Your Email Address",
                "welcome-email",
                templateModel);

    }

    @Override
    @Transactional
    public String generateConfirmEmailToken(User user) {
        String newConfirmEmailToken = TokenGenerator.generateToken6NumCharToken();
        Instant newExpiration = Instant.now().plusSeconds(60 * 15);

        Optional<ConfirmEmailOTT> existingOpt = confirmEmailOTTRepository.findByUser(user);

        ConfirmEmailOTT tokenToSave;
        if (existingOpt.isPresent()) {
            tokenToSave = existingOpt.get();
            tokenToSave.setToken(newConfirmEmailToken);
            tokenToSave.setExpiration(newExpiration);
        } else {
            tokenToSave = ConfirmEmailOTT.builder().user(user).token(newConfirmEmailToken).expiration(newExpiration)
                    .build();
        }

        confirmEmailOTTRepository.save(tokenToSave);
        return newConfirmEmailToken;
    }

}
