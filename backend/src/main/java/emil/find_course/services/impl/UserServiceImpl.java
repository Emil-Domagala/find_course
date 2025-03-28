package emil.find_course.services.impl;

import org.springframework.stereotype.Service;

import emil.find_course.domains.entities.user.User;
import emil.find_course.repositories.UserRepository;
import emil.find_course.services.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(()-> new EntityNotFoundException("User not found"));
    }

}
