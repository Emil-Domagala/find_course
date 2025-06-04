package emil.find_course.auth;

import org.springframework.security.core.Authentication;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import emil.find_course.auth.dto.request.UserLoginRequest;
import emil.find_course.auth.dto.request.UserRegisterRequest;
import emil.find_course.auth.dto.response.AuthResponse;
import emil.find_course.auth.email.EmailVerificationService;
import emil.find_course.common.exception.FieldValidationException;
import emil.find_course.common.exception.UnauthorizedException;
import emil.find_course.common.security.jwt.JwtUtils;
import emil.find_course.common.security.jwt.UserDetailsImpl;
import emil.find_course.user.entity.User;
import emil.find_course.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    @Value("${jwt.refreshToken.expiration}")
    private int jwtRefreshTokenExpirationMs;

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    private final EmailVerificationService emailVerificationService;

    @Override
    public AuthResponse loginUser(UserLoginRequest userLoginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        userLoginRequest.getEmail().trim(), userLoginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
        String token = jwtUtils.generateToken(userPrincipal);
        String refreshToken = jwtUtils.generateRefreshToken(userPrincipal.getUser());

        return new AuthResponse(token, refreshToken);
    }

    @Transactional
    @Override
    public User registerUser(UserRegisterRequest userRegisterRequest) {
        boolean existsByEmail = userRepository.existsByEmail(userRegisterRequest.getEmail());

        if (existsByEmail) {
            throw new FieldValidationException("email", "email is already taken");
        }

        User user = User.builder()
                .email(userRegisterRequest.getEmail().trim())
                .password(passwordEncoder.encode(userRegisterRequest.getPassword()))
                .username(userRegisterRequest.getUsername().trim())
                .userLastname(userRegisterRequest.getUserLastname().trim()).build();

        User newUser = userRepository.save(user);
        emailVerificationService.sendVerificationEmail(newUser);
        return newUser;
    }

    @Override
    public String refreshAuthToken(String recivedRefreshToken) {
        if (recivedRefreshToken == null) {
            throw new UnauthorizedException("Didn't recive refresh token");
        }
        try {
            if (!jwtUtils.validateRefreshToken(recivedRefreshToken)) {
                throw new UnauthorizedException("Invalid refresh token");
            }
            String email = jwtUtils.getUserEmailFromJwtToken(recivedRefreshToken);
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));

            String newAuthToken = jwtUtils.generateToken(user);
            return newAuthToken;

        } catch (UnauthorizedException ex) {
            throw new UnauthorizedException(ex.getMessage());
        } catch (EntityNotFoundException ex) {
            throw new UnauthorizedException(ex.getMessage());
        } catch (Exception ex) {
            throw new UnauthorizedException("Invalid refresh token", ex);
        }
    }
    
}
