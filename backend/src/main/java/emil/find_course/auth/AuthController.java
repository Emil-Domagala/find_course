package emil.find_course.auth;

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

import emil.find_course.auth.dto.request.UserLoginRequest;
import emil.find_course.auth.dto.request.UserRegisterRequest;
import emil.find_course.auth.dto.response.AuthResponse;
import emil.find_course.common.security.jwt.JwtUtils;
import emil.find_course.common.util.CookieHelper;
import emil.find_course.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Tag(name = "Auth Controller", description = "Endpoints for authentication")
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AuthController {

        @Value("${cookie.auth.authToken.name}")
        private String authCookieName;

        @Value("${jwt.authToken.expiration}")
        private int authExpiration;

        @Value("${cookie.auth.refreshToken.name}")
        private String refreshCookieName;
        @Value("${jwt.refreshToken.expiration}")
        private int refreshCookieExpiration;

        @Value("${cookie.auth.accessCookie.name}")
        private String accessCookieName;

        private final JwtUtils jwtUtils;
        private final AuthService authService;
        private final CookieHelper cookieHelper;

        @Operation(summary = "Register user")
        @PostMapping("/public/register")
        public ResponseEntity<AuthResponse> register(@Validated @RequestBody UserRegisterRequest request) {
                User user = authService.registerUser(request);
                AuthResponse auth = new AuthResponse(jwtUtils.generateToken(user), jwtUtils.generateRefreshToken(user));

                ResponseCookie authTokenCookie = cookieHelper.setCookie(authCookieName, auth.token(), authExpiration,
                                "/");
                ResponseCookie refreshCookie = cookieHelper.setCookie(
                                refreshCookieName, auth.refreshToken(),
                                refreshCookieExpiration,
                                "/api/v1/public/refresh-token");

                ResponseCookie roleCookie = cookieHelper.setCookie(accessCookieName, auth.token(),refreshCookieExpiration, "/");

                // Set cookies in separate headers for clarity and standard compliance
                return ResponseEntity.ok()
                                .header(HttpHeaders.SET_COOKIE, authTokenCookie.toString())
                                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                                .header(HttpHeaders.SET_COOKIE, roleCookie.toString())
                                .body(auth);
        }

        @Operation(summary = "Login user")
        @PostMapping("/public/login")
        public ResponseEntity<AuthResponse> login(@Validated @RequestBody UserLoginRequest request) {
                AuthResponse auth = authService.loginUser(request);

                ResponseCookie authTokenCookie = cookieHelper.setCookie(authCookieName, auth.token(), authExpiration,
                                "/");
                ResponseCookie refreshCookie = cookieHelper.setCookie(
                                refreshCookieName, auth.refreshToken(), refreshCookieExpiration,
                                "/api/v1/public/refresh-token");
                ResponseCookie roleCookie = cookieHelper.setCookie(accessCookieName, auth.token(),
                                refreshCookieExpiration, "/");

                // Set cookies in separate headers
                return ResponseEntity.ok()
                                .header(HttpHeaders.SET_COOKIE, authTokenCookie.toString())
                                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                                .header(HttpHeaders.SET_COOKIE, roleCookie.toString())
                                .body(auth);
        }

        @Operation(summary = "Logout user", description = "Sets all cookies to expire")
        @PostMapping("/public/logout")
        public ResponseEntity<Void> logout() {
                ResponseCookie deleteCookie = cookieHelper.setCookie(authCookieName, "", 0, "/");
                ResponseCookie deleteRefreshCookie = cookieHelper.setCookie(refreshCookieName, "", 0,
                                "/api/v1/public/refresh-token");
                ResponseCookie deleteRoleCookie = cookieHelper.setCookie(accessCookieName, "", 0, "/");

                // Set cookies in separate headers
                return ResponseEntity.noContent()
                                .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                                .header(HttpHeaders.SET_COOKIE, deleteRefreshCookie.toString())
                                .header(HttpHeaders.SET_COOKIE, deleteRoleCookie.toString())
                                .build();
        }

        @Operation(summary = "Refresh token", description = "Sends new auth token")
        @PostMapping("/public/refresh-token")
        public ResponseEntity<Void> refreshCookie(
                        @CookieValue(name = "${cookie.auth.refreshToken.name}", required = false) String refreshToken) {

                log.info(refreshToken);
                log.info("Refresh token request recived");
                String authToken = authService.refreshAuthToken(refreshToken);
                ResponseCookie authTokenCookie = cookieHelper.setCookie(authCookieName, authToken, authExpiration, "/");

                return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,
                                authTokenCookie.toString()).build();
        }

}
