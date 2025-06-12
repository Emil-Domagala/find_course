package emil.find_course.auth.resetPassword;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import emil.find_course.auth.resetPassword.entity.ResetPasswordOTT;
import emil.find_course.auth.resetPassword.repository.ResetPasswordOTTRepository;
import emil.find_course.common.service.EmailService;
import emil.find_course.common.util.TokenGenerator;
import emil.find_course.user.entity.User;
import emil.find_course.user.repository.UserRepository;
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
            resetPasswordOTTRepository.delete(resetPasswordOTT);
            throw new IllegalArgumentException("Token has expired");
        }
        User user = resetPasswordOTT.getUser();
        if (user.isEmailVerified() == false) {
            throw new IllegalArgumentException("Email is not verified");
        }
        if (passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Old password cannot be used");
        }
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
        resetPasswordOTTRepository.delete(resetPasswordOTT);
    }

    @Override
    @Transactional
    public void sendResetPasswordEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Couldn't find user"));

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
                    "Reset Password Request",
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