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
import lombok.RequiredArgsConstructor;

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

        private final JwtUtils jwtUtils;
        private final AuthService authService;
        private final CookieHelper cookieHelper;

        @PostMapping("/public/register")
        public ResponseEntity<AuthResponse> register(@Validated @RequestBody UserRegisterRequest request) {
                User user = authService.registerUser(request);
                AuthResponse auth = new AuthResponse(jwtUtils.generateToken(user), jwtUtils.generateRefreshToken(user));

                ResponseCookie cookie = cookieHelper.setCookie(authCookieName, auth.token(), authExpiration,
                                "/");
                ResponseCookie refreshCookie = cookieHelper.setCookie(
                                refreshCookieName, auth.refreshToken(),
                                refreshCookieExpiration,
                                "/api/v1/public/refresh-token");

                return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString(), refreshCookie.toString())
                                .body(auth);
        }

        @PostMapping("/public/login")
        public ResponseEntity<AuthResponse> login(@Validated @RequestBody UserLoginRequest request) {
                AuthResponse auth = authService.loginUser(request);

                ResponseCookie cookie = cookieHelper.setCookie(authCookieName, auth.token(), authExpiration,
                                "/");
                ResponseCookie refreshCookie = cookieHelper.setCookie(
                                refreshCookieName, auth.refreshToken(), refreshCookieExpiration,
                                "/api/v1/public/refresh-token");

                return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString(), refreshCookie.toString())
                                .body(auth);
        }

        @PostMapping("/public/logout")
        public ResponseEntity<Void> logout() {
                ResponseCookie deleteCookie = cookieHelper.setCookie(authCookieName, "", 0, "/");
                ResponseCookie deleteRefreshCookie = cookieHelper.setCookie(refreshCookieName, "", 0,
                                "/api/v1/public/refresh-token");

                return ResponseEntity.noContent()
                                .header(HttpHeaders.SET_COOKIE, deleteCookie.toString(), deleteRefreshCookie.toString())
                                .build();
        }

        // refresh-cookie
        @PostMapping("/public/refresh-token")
        public ResponseEntity<Void> refreshCookie(@CookieValue(name = "${cookie.auth.refreshToken.name}", required = false) String refreshToken) {

                String authToken = authService.refreshAuthToken(refreshToken);
                ResponseCookie cookie = cookieHelper.setCookie(authCookieName, authToken, authExpiration, "/");

                return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,
                                cookie.toString()).build();
        }

}
