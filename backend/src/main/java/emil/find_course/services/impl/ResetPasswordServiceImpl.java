package emil.find_course.services.impl;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import emil.find_course.domains.entities.ResetPasswordOTT;
import emil.find_course.domains.entities.user.User;
import emil.find_course.repositories.ResetPasswordOTTRepository;
import emil.find_course.repositories.UserRepository;
import emil.find_course.services.EmailService;
import emil.find_course.services.ResetPasswordService;
import emil.find_course.utils.TokenGenerator;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ResetPasswordServiceImpl implements ResetPasswordService {

    @Value("${frontend.domain}")
    private String frontendDomain;

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final ResetPasswordOTTRepository resetPasswordOTTRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void resetPassword(String token, String password) {
        ResetPasswordOTT resetPasswordOTT = resetPasswordOTTRepository.findByToken(token)
                .orElseThrow(() -> new EntityNotFoundException("Couldn't find token"));

        if (resetPasswordOTT.getExpiration().isBefore(Instant.now())) {
            System.out.println("Token has expired");
            resetPasswordOTTRepository.delete(resetPasswordOTT);
            throw new IllegalArgumentException("Token has expired");
        }
        if (!resetPasswordOTT.getToken().equals(token)) {
            System.out.println("Invalid token");
            throw new IllegalArgumentException("Invalid token");
        }
        User user = resetPasswordOTT.getUser();
        if (user.isEmailVerified() == false) {
            System.out.println("Email is not verified");
            throw new IllegalArgumentException("Email is not verified");
        }
        if (passwordEncoder.matches(password, user.getPassword())) {
            System.out.println("Old password cannot be used");
            throw new IllegalArgumentException("Old password cannot be used");
        }
        System.out.println("Reset password without errors");
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
        resetPasswordOTTRepository.delete(resetPasswordOTT);
    }

    @Override
    @Transactional
    public void sendResetPasswordEmail(String email) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user.isEmailVerified() == false) {
            throw new IllegalArgumentException("Email is not verified");
        }

        if (user != null) {
            String resetPasswordToken = generateResetPasswordToken(user);

            Map<String, Object> templateModel = new HashMap<>();
            templateModel.put("user", user);
            templateModel.put("resetLink", frontendDomain + "/auth/reset-password?token=" + resetPasswordToken);

            emailService.sendHtmlEmail(
                    user.getEmail(),
                    "Verify Your Email Address",
                    "password-reset",
                    templateModel);
        }
    }

    @Override
    @Transactional
    public String generateResetPasswordToken(User user) {
        String newResetPasswordToken = TokenGenerator.generateToken24NumCharToken();
        Instant newExpiration = Instant.now().plusSeconds(60 * 60);

        Optional<ResetPasswordOTT> existingOpt = resetPasswordOTTRepository.findByUser(user);

        ResetPasswordOTT tokenToSave;
        if (existingOpt.isPresent()) {
            tokenToSave = existingOpt.get();
            tokenToSave.setToken(newResetPasswordToken);
            tokenToSave.setExpiration(newExpiration);
        } else {
            tokenToSave = ResetPasswordOTT.builder().user(user).token(newResetPasswordToken).expiration(newExpiration)
                    .build();
        }

        resetPasswordOTTRepository.save(tokenToSave);
        return newResetPasswordToken;
    }
}
