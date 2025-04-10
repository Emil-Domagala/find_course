package emil.find_course.controllers;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import emil.find_course.domains.dto.AuthResponse;
import emil.find_course.domains.entities.user.User;
import emil.find_course.domains.requestDto.UserLoginRequest;
import emil.find_course.domains.requestDto.UserRegisterRequest;
import emil.find_course.security.jwt.JwtUtils;
import emil.find_course.services.AuthService;
import emil.find_course.services.EmailVerificationService;
import emil.find_course.services.UserService;
import emil.find_course.utils.CookieHelper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AuthController {

        @Value("${cookie.auth.authToken.name}")
        private String authCookieName;

        @Value("${jwt.authToken.expiration}")
        private int cookieExpiration;

        @Value("${cookie.auth.refreshToken.name}")
        private String refreshCookieName;
        @Value("${jwt.refreshToken.expiration}")
        private int refreshCookieExpiration;

        private final JwtUtils jwtUtils;
        private final AuthService authService;
        private final UserService userService;
        private final EmailVerificationService emailVerificationService;

        @PostMapping("/public/register")
        public ResponseEntity<AuthResponse> register(@Validated @RequestBody UserRegisterRequest request) {
                User user = authService.registerUser(request);
                String refreshToken = jwtUtils.generateRefreshToken(user);
                AuthResponse auth = new AuthResponse(jwtUtils.generateToken(user), refreshToken);
                emailVerificationService.generateConfirmEmailToken(user);

                ResponseCookie cookie = CookieHelper.setCookieHelper(authCookieName, auth.token(), cookieExpiration,
                                "/");
                ResponseCookie refreshCookie = CookieHelper.setCookieHelper(
                                refreshCookieName, refreshToken, refreshCookieExpiration,
                                "/api/v1/public/refresh-token");

                return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString(), refreshCookie.toString())
                                .body(auth);
        }

        @PostMapping("/public/login")
        public ResponseEntity<AuthResponse> login(@RequestBody UserLoginRequest request, HttpServletResponse response) {
                AuthResponse auth = authService.loginUser(request);

                ResponseCookie cookie = CookieHelper.setCookieHelper(authCookieName, auth.token(), cookieExpiration,
                                "/");
                ResponseCookie refreshCookie = CookieHelper.setCookieHelper(
                                refreshCookieName, auth.refreshToken(), refreshCookieExpiration,
                                "/api/v1/public/refresh-token");

                System.out.println("refreshCookie:");
                System.out.println(refreshCookie.toString());
                System.out.println("AuthCookie:");
                System.out.println(cookie.toString());

                return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString(), refreshCookie.toString())
                                .body(auth);
        }

        @PostMapping("/public/logout")
        public ResponseEntity<Void> logout() {
                ResponseCookie deleteCookie = CookieHelper.setCookieHelper(authCookieName, "", 0, "/");
                ResponseCookie deleteRefreshCookie = CookieHelper.setCookieHelper(refreshCookieName, "", 0,
                                "/api/v1/public/refresh-cookie");

                return ResponseEntity.noContent()
                                .header(HttpHeaders.SET_COOKIE, deleteCookie.toString(), deleteRefreshCookie.toString())
                                .build();
        }

        // refresh-cookie
        @PostMapping("/public/refresh-token")
        public ResponseEntity<Void> refreshCookie(
                        @CookieValue(name = "${cookie.auth.refreshToken.name}") String refreshToken) {

                System.out.println(refreshToken);

                String authToken = authService.refreshAuthToken(refreshToken);
                ResponseCookie cookie = CookieHelper.setCookieHelper(authCookieName, authToken, cookieExpiration, "/");

                return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,
                                cookie.toString()).build();
        }

        // It will be deleted

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
}
