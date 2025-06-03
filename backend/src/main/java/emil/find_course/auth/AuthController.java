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

        @Value("${domain.name}")
        private String domainName;
        @Value("${spring.profiles.active}")
        private String springProfile;

        private final JwtUtils jwtUtils;
        private final AuthService authService;

        @PostMapping("/public/register")
        public ResponseEntity<AuthResponse> register(@Validated @RequestBody UserRegisterRequest request) {
                User user = authService.registerUser(request);
                String refreshToken = jwtUtils.generateRefreshToken(user);
                AuthResponse auth = new AuthResponse(jwtUtils.generateToken(user), refreshToken);

                ResponseCookie cookie = CookieHelper.setCookieHelper(authCookieName, auth.token(), cookieExpiration,
                                "/", springProfile, domainName);
                ResponseCookie refreshCookie = CookieHelper.setCookieHelper(
                                refreshCookieName, refreshToken, refreshCookieExpiration,
                                "/", springProfile, domainName);

                return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString(), refreshCookie.toString())
                                .body(auth);
        }

        @PostMapping("/public/login")
        public ResponseEntity<AuthResponse> login(@RequestBody UserLoginRequest request, HttpServletResponse response) {
                AuthResponse auth = authService.loginUser(request);

                ResponseCookie cookie = CookieHelper.setCookieHelper(authCookieName, auth.token(), cookieExpiration,
                                "/", springProfile, domainName);
                ResponseCookie refreshCookie = CookieHelper.setCookieHelper(
                                refreshCookieName, auth.refreshToken(), refreshCookieExpiration,
                                "/", springProfile, domainName);

                return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString(), refreshCookie.toString())
                                .body(auth);
        }

        @PostMapping("/public/logout")
        public ResponseEntity<Void> logout() {
                ResponseCookie deleteCookie = CookieHelper.setCookieHelper(authCookieName, "", 0, "/", springProfile,
                                domainName);
                ResponseCookie deleteRefreshCookie = CookieHelper.setCookieHelper(refreshCookieName, "", 0,
                                "/", springProfile, domainName);

                return ResponseEntity.noContent()
                                .header(HttpHeaders.SET_COOKIE, deleteCookie.toString(), deleteRefreshCookie.toString())
                                .build();
        }

        // refresh-cookie
        @PostMapping("/public/refresh-token")
        public ResponseEntity<Void> refreshCookie(
                        @CookieValue(name = "${cookie.auth.refreshToken.name}") String refreshToken) {

                String authToken = authService.refreshAuthToken(refreshToken);
                ResponseCookie cookie = CookieHelper.setCookieHelper(authCookieName, authToken, cookieExpiration, "/",
                                springProfile, domainName);

                return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,
                                cookie.toString()).build();
        }

}
