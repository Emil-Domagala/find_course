package emil.find_course.services.impl;

import java.security.Principal;
import java.util.Optional;
import java.util.Set;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import emil.find_course.domains.entities.BecomeTeacher;
import emil.find_course.domains.entities.course.Course;
import emil.find_course.domains.entities.user.User;
import emil.find_course.domains.requestDto.RequestUpdateUser;
import emil.find_course.repositories.BecomeTeacherRepository;
import emil.find_course.repositories.UserRepository;
import emil.find_course.services.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final BecomeTeacherRepository becomeTeacherRepository;

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    @Transactional
    @Override
    public String getRoles(Principal principal) {
        User user = findByEmail(principal.getName());
        return user.getRoles().stream().map(role -> "ROLE_" + role.name()).toList().toString()
                .replace(" ", "").replace(",", "|");
    }

    @Override
    public User updateUser(RequestUpdateUser requestUpdateUser, User user) {

        user.setUsername(requestUpdateUser.getUsername());
        user.setUserLastname(requestUpdateUser.getUserLastname());
        if (requestUpdateUser.getPassword() != null
                && !requestUpdateUser.getPassword().isEmpty()
                && requestUpdateUser.getPassword().length() > 6
                && requestUpdateUser.getPassword().length() < 30) {

            user.setPassword(passwordEncoder.encode(requestUpdateUser.getPassword()));
        }
        if (requestUpdateUser.getImage() != null) {
            user.setImageUrl(requestUpdateUser.getImage());
        }

        return userRepository.save(user);

    }

    @Override
    public void deleteUser(User user) {
        userRepository.delete(user);
    }

    @Override
    public Optional<BecomeTeacher> getBecomeTeacherRequest(User user) {
        return becomeTeacherRepository.findByUser(user);
    }

    @Override
    public BecomeTeacher createBecomeTeacherRequest(User user) {
        Optional<BecomeTeacher> becomeTeacherOpt = becomeTeacherRepository.findByUser(user);
        if (becomeTeacherOpt.isPresent()) {
            new IllegalArgumentException("You already sent become teacher request");
        }
        BecomeTeacher newBecomeTeacher = new BecomeTeacher();
        newBecomeTeacher.setUser(user);
        return becomeTeacherRepository.save(newBecomeTeacher);
    }

    @Override
    public void grantAccessToCourse(User user, Set<Course> courses) {
        user.getEnrollmentCourses().addAll(courses);
        userRepository.save(user);
    }

}
