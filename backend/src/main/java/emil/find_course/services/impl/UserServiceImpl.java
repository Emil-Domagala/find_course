package emil.find_course.services.impl;

import org.springframework.stereotype.Service;

import emil.find_course.domains.entities.user.User;
import emil.find_course.domains.requestDto.UserRegisterRequest;
import emil.find_course.exceptions.FieldValidationException;
import emil.find_course.repositories.UserRepository;
import emil.find_course.services.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Transactional
    @Override
    public User registerUser(UserRegisterRequest userRegisterRequest) {
        boolean existsByEmail = userRepository.existsByEmail(userRegisterRequest.getEmail());
        if (existsByEmail) {
            throw new FieldValidationException("email", "email is already taken");
        }
        User user = User.builder()
                .email(userRegisterRequest.getEmail())
                .password(userRegisterRequest.getPassword())
                .username(userRegisterRequest.getUsername())
                .userLastname(userRegisterRequest.getUserLastname()).build();

        return userRepository.save(user);

    }

}
