package emil.find_course.IntegrationTests.teacherApplication;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import emil.find_course.IntegrationTests.user.UserFactory;
import emil.find_course.user.entity.User;
import emil.find_course.user.enums.Role;
import emil.find_course.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class PrepareAdminUtil {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User prepareAdmin() {
        User user = UserFactory.createVerifiedUser();
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(Set.of(Role.USER, Role.TEACHER, Role.ADMIN));
        userRepository.save(user);

        User savedUser = userRepository.findByEmail(user.getEmail()).orElseThrow();
        assertThat(savedUser.isEmailVerified()).isTrue();
        assertThat(savedUser.getRoles().contains(Role.TEACHER));
        assertThat(savedUser.getRoles().contains(Role.ADMIN));
        assertThat(savedUser.getRoles().contains(Role.USER));
        return savedUser;
    }

    public User prepareUniqueAdmin() {
        User user = UserFactory.createUniqueVerifiedUser();
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(Set.of(Role.USER, Role.TEACHER, Role.ADMIN));
        userRepository.save(user);

        User savedUser = userRepository.findByEmail(user.getEmail()).orElseThrow();
        assertThat(savedUser.isEmailVerified()).isTrue();
        assertThat(savedUser.getRoles().contains(Role.TEACHER));
        assertThat(savedUser.getRoles().contains(Role.ADMIN));
        assertThat(savedUser.getRoles().contains(Role.USER));
        return savedUser;
    }
}
