package emil.find_course.services.impl;

import org.springframework.security.core.Authentication;

import java.util.stream.Collectors;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import emil.find_course.domains.dto.AuthResponse;
import emil.find_course.domains.entities.user.User;
import emil.find_course.domains.requestDto.UserLoginRequest;
import emil.find_course.domains.requestDto.UserRegisterRequest;
import emil.find_course.exceptions.FieldValidationException;
import emil.find_course.repositories.UserRepository;
import emil.find_course.security.jwt.JwtUtils;
import emil.find_course.security.jwt.UserPrincipal;
import emil.find_course.services.AuthService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;

    @Override
    public AuthResponse loginUser(UserLoginRequest userLoginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        userLoginRequest.getEmail(), userLoginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        String token = jwtUtils.generateToken(userPrincipal);
        String roles = authentication.getAuthorities().toString().replace(" ", "").replace(",", "|");

        return new AuthResponse(token, roles);
    }

    @Transactional
    @Override
    public AuthResponse registerUser(UserRegisterRequest userRegisterRequest) {
        boolean existsByEmail = userRepository.existsByEmail(userRegisterRequest.getEmail());

        if (existsByEmail) {
            throw new FieldValidationException("email", "email is already taken");
        }

        User user = User.builder()
                .email(userRegisterRequest.getEmail())
                .password(passwordEncoder.encode(userRegisterRequest
                        .getPassword()))
                .username(userRegisterRequest.getUsername())
                .userLastname(userRegisterRequest.getUserLastname()).build();

        User savedUser = userRepository.save(user);

        if (savedUser == null) {
            throw new IllegalArgumentException("Problem saving to database");
        }
        
        return loginRegisteredUser(savedUser);

    }

    private AuthResponse loginRegisteredUser(User user) {
        String token = jwtUtils.generateToken(user);
        String roles = user.getRoles().stream().map(role -> "ROLE_" + role.name()).toList().toString()
                .replace(" ", "").replace(",", "|");

        return new AuthResponse(token, roles);
    }

}
