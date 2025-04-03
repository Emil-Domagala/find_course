package emil.find_course.controllers;

import java.security.Principal;

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
import emil.find_course.services.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/public")
@RequiredArgsConstructor
public class AuthController {

    @Value("${cookie.auth.name}")
    private String authCookieName;
    @Value("${cookie.roles.name}")
    private String roleCookieName;
    @Value("${cookie.expiration}")
    private int cookieExpiration;
    @Value("${frontend.domain}")
    private String frontendDomain;

    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Validated @RequestBody UserRegisterRequest request) {
        AuthResponse auth = authService.registerUser(request);

        ResponseCookie cookie = setCookieHelper(authCookieName, auth.token(), cookieExpiration);
        ResponseCookie roleCookie = setCookieHelper(roleCookieName, auth.roles(), cookieExpiration);

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString(), roleCookie.toString()).body(auth);

    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody UserLoginRequest request, HttpServletResponse response) {
        AuthResponse auth = authService.loginUser(request);

        ResponseCookie cookie = setCookieHelper(authCookieName, auth.token(), cookieExpiration);
        ResponseCookie roleCookie = setCookieHelper(roleCookieName, auth.roles(), cookieExpiration);

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString(), roleCookie.toString()).body(auth);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        ResponseCookie deleteCookie = setCookieHelper(authCookieName, "", 0);
        ResponseCookie deleteRoleCookie = setCookieHelper(roleCookieName, "", 0);

        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, deleteCookie.toString(),
                        deleteRoleCookie.toString())
                .build();
    }

    @PostMapping("/get-roles")
    public ResponseEntity<String> getRoles(Principal principal) {
        if (principal == null) {
            return ResponseEntity.ok("");
        }
        String roles = userService.getRoles(principal);
        return ResponseEntity.ok(roles);
    }

    // Helper functions

    private ResponseCookie setCookieHelper(String cookieName, String value, int maxAge) {
        return ResponseCookie.from(cookieName, value)
                .httpOnly(true)
                .secure(true)
                // .domain(frontendDomain)
                .sameSite("Strict")
                .path("/")
                .maxAge(maxAge)
                .build();
    }

}
