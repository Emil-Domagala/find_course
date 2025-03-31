package emil.find_course.controllers;

import org.apache.tomcat.util.http.SameSiteCookies;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import emil.find_course.domains.dto.AuthResponse;
import emil.find_course.domains.requestDto.UserLoginRequest;
import emil.find_course.domains.requestDto.UserRegisterRequest;
import emil.find_course.services.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/public")
@RequiredArgsConstructor
public class AuthController {

    @Value("${cookie.name}")
    private String cookieName;
    @Value("${cookie.expiration}")
    private int cookieExpiration;

    private final AuthService authService;

    @PostMapping("/register")
    public String register(@Validated @RequestBody UserRegisterRequest request) {
        authService.registerUser(request);

        return "User registered successfully!";
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody UserLoginRequest request, HttpServletResponse response) {

        AuthResponse auth = authService.loginUser(request);

        ResponseCookie cookie = ResponseCookie.from(cookieName, auth.token())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(cookieExpiration)
                .sameSite(SameSiteCookies.STRICT.toString())
                .build();
        ;

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(auth);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        ResponseCookie deleteCookie = ResponseCookie.from(cookieName, "")
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                .build();
    }

}
