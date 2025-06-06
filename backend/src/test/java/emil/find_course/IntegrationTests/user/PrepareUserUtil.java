package emil.find_course.IntegrationTests.user;

import emil.find_course.user.entity.User;
import emil.find_course.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import static org.assertj.core.api.Assertions.assertThat;

@Component
@RequiredArgsConstructor
public class PrepareUserUtil {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User prepareVerifiedUser() {
        User user = UserFactory.createVerifiedUser();
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        User savedUser = userRepository.findByEmail(user.getEmail()).orElseThrow();
        assertThat(savedUser.isEmailVerified()).isTrue();
        return savedUser;
    }

    public User prepareVerifiedUser(String email, String name) {
        User user = UserFactory.createVerifiedUser(email, name);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        User savedUser = userRepository.findByEmail(user.getEmail()).orElseThrow();
        assertThat(savedUser.isEmailVerified()).isTrue();
        return savedUser;
    }

    public User prepareVerifiedUserWithImage() {
        User user = UserFactory.createVerifiedUser();
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setImageUrl("https://example.com/image.jpg");
        userRepository.save(user);

        User savedUser = userRepository.findByEmail(user.getEmail()).orElseThrow();
        assertThat(savedUser.isEmailVerified()).isTrue();
        assertThat(savedUser.getImageUrl()).isNotNull();
        return savedUser;
    }

    public User prepareNotVerifiedUser() {
        User user = UserFactory.createNotVerifiedUser();
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        User savedUser = userRepository.findByEmail(user.getEmail()).orElseThrow();
        assertThat(savedUser.isEmailVerified()).isFalse();
        return savedUser;
    }
}
