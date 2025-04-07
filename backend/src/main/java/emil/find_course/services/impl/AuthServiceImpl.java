package emil.find_course.services.impl;

import org.springframework.security.core.Authentication;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import emil.find_course.domains.dto.AuthResponse;
import emil.find_course.domains.entities.ConfirmEmailOTT;
import emil.find_course.domains.entities.user.User;
import emil.find_course.domains.requestDto.UserLoginRequest;
import emil.find_course.domains.requestDto.UserRegisterRequest;
import emil.find_course.exceptions.FieldValidationException;
import emil.find_course.repositories.ConfirmEmailOTTRepository;
import emil.find_course.repositories.UserRepository;
import emil.find_course.security.jwt.JwtUtils;
import emil.find_course.security.jwt.UserDetailsImpl;
import emil.find_course.services.AuthService;
import emil.find_course.utils.TokenGenerator;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final ConfirmEmailOTTRepository confirmEmailOTTRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;

    @Override
    public AuthResponse loginUser(UserLoginRequest userLoginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        userLoginRequest.getEmail().trim(), userLoginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
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
                .email(userRegisterRequest.getEmail()
                        .trim())
                .password(passwordEncoder.encode(userRegisterRequest
                        .getPassword()))
                .username(userRegisterRequest.getUsername().trim())
                .userLastname(userRegisterRequest.getUserLastname().trim()).build();

        User savedUser = userRepository.save(user);

        if (savedUser == null) {
            throw new IllegalArgumentException("Problem saving to database");
        }

        generateConfirmEmailToken(savedUser);

        return loginRegisteredUser(savedUser);

    }

    @Override
    public void resendConfirmEmail(User user) {
        if (user.isEmailVerified()) {
            throw new IllegalArgumentException("Email is already verified");
        }
        Optional<ConfirmEmailOTT> oldConfirmEmailOTT = confirmEmailOTTRepository.findByUser(user);

        if (oldConfirmEmailOTT.isPresent()) {
            confirmEmailOTTRepository.delete(oldConfirmEmailOTT.get());
        }

        String confirmEmailToken = generateConfirmEmailToken(user);
    }

    @Transactional
    public AuthResponse validateEmail(User user, String token) {
        if (user.isEmailVerified()) {
            throw new IllegalArgumentException("Email is already verified");
        }
        ConfirmEmailOTT confirmOTT = confirmEmailOTTRepository.findByUser(user)
                .orElseThrow(() -> new EntityNotFoundException("Couldn't find token"));

        if (confirmOTT.getExpiration().isBefore(LocalDateTime.now())) {
            confirmEmailOTTRepository.delete(confirmOTT);
            throw new IllegalArgumentException("Token has expired");
        }
        if (!confirmOTT.getToken().equals(token)) {
            throw new IllegalArgumentException("Invalid token");
        }
        user.setEmailVerified(true);
        User savedUser = userRepository.save(user);
        confirmEmailOTTRepository.delete(confirmOTT);
        return loginRegisteredUser(savedUser);
    }

    // Helper function

    private AuthResponse loginRegisteredUser(User user) {
        String token = jwtUtils.generateToken(user);
        String roles = user.getRoles().stream().map(role -> "ROLE_" + role.name()).toList().toString()
                .replace(" ", "").replace(",", "|");

        return new AuthResponse(token, roles);
    }

    @Transactional
    private String generateConfirmEmailToken(User user) {
        Optional<ConfirmEmailOTT> confirmOTT = confirmEmailOTTRepository.findByUser(user);
        if (confirmOTT.isPresent()) {
            confirmEmailOTTRepository.delete(confirmOTT.get());
        }

        String confirmEmailToken = TokenGenerator.generateToken6NumCharToken();

        ConfirmEmailOTT confirmEmailOTT = ConfirmEmailOTT.builder().user(user).token(confirmEmailToken)
                .expiration(LocalDateTime.now().plusMinutes(15)).build();

        confirmEmailOTTRepository.save(confirmEmailOTT);
        return confirmEmailToken;
    }
}
