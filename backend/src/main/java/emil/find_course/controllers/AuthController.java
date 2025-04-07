package emil.find_course.controllers;

import java.security.Principal;

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
import emil.find_course.domains.requestDto.RequestConfirmEmailOTT;
import emil.find_course.domains.requestDto.UserLoginRequest;
import emil.find_course.domains.requestDto.UserRegisterRequest;
import emil.find_course.services.AuthService;
import emil.find_course.services.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
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

    @PostMapping("/public/register")
    public ResponseEntity<AuthResponse> register(@Validated @RequestBody UserRegisterRequest request) {
        AuthResponse auth = authService.registerUser(request);

        ResponseCookie cookie = setCookieHelper(authCookieName, auth.token(), cookieExpiration);
        ResponseCookie roleCookie = setCookieHelper(roleCookieName, auth.roles(), cookieExpiration);

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString(), roleCookie.toString()).body(auth);

    }

    @PostMapping("/public/login")
    public ResponseEntity<AuthResponse> login(@RequestBody UserLoginRequest request, HttpServletResponse response) {
        AuthResponse auth = authService.loginUser(request);

        ResponseCookie cookie = setCookieHelper(authCookieName, auth.token(), cookieExpiration);
        ResponseCookie roleCookie = setCookieHelper(roleCookieName, auth.roles(), cookieExpiration);

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString(), roleCookie.toString()).body(auth);
    }

    @PostMapping("/public/logout")
    public ResponseEntity<Void> logout() {
        ResponseCookie deleteCookie = setCookieHelper(authCookieName, "", 0);
        ResponseCookie deleteRoleCookie = setCookieHelper(roleCookieName, "", 0);

        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, deleteCookie.toString(),
                        deleteRoleCookie.toString())
                .build();
    }

    // Confirm Email

    @PostMapping("/confirm-email")
    public ResponseEntity<AuthResponse> confirmEmail(Principal principal,
            @Validated @RequestBody RequestConfirmEmailOTT token) {

        AuthResponse auth = authService.validateEmail(userService.findByEmail(principal.getName()), token.getToken());
        ResponseCookie cookie = setCookieHelper(authCookieName, auth.token(), cookieExpiration);
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(auth);
    }

    @PostMapping("/confirm-email/resend")
    public ResponseEntity<Void> resendConfirmEmail(Principal principal) {
        authService.resendConfirmEmail(userService.findByEmail(principal.getName()));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/get-roles")
    public ResponseEntity<String> getRoles(Principal principal) {

        if (principal == null) {
            System.out.println("no principal");
            return ResponseEntity.ok("");
        }
        System.out.println("get-roles");
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
