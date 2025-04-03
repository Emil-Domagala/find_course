package emil.find_course.services.impl;

import java.security.Principal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        return userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    @Transactional
    @Override
    public String getRoles(Principal principal) {
        User user = findByEmail(principal.getName());
        return user.getRoles().stream().map(role -> "ROLE_" + role.name()).toList().toString()
                .replace(" ", "").replace(",", "|");
    }

}
