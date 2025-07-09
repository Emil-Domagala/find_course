package emil.find_course.common.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import emil.find_course.common.security.jwt.JwtUtils;
import emil.find_course.user.entity.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CookieHelper {

        @Value("${spring.profiles.active}")
        private String springProfile;

        @Value("${domain.name}")
        private String domainName;

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

        public ResponseCookie setCookie(String cookieName, String value, int maxAgeMilis, String path) {
                boolean isSecure = !"local".equals(springProfile);
                return ResponseCookie.from(cookieName, value)
                                .httpOnly(true)
                                .secure(isSecure)
                                .domain("." + domainName)
                                .sameSite("Strict")
                                .path(path)
                                .maxAge(maxAgeMilis / 1000)
                                .build();
        }

        public AllAuthCookies createAllAuthCookies(User user) {
                return new AllAuthCookies(user);
        }

        @Getter
        public class AllAuthCookies {
                private ResponseCookie authCookie;
                private ResponseCookie refreshCookie;
                private ResponseCookie accessCookie;

                AllAuthCookies(User user) {
                        this.authCookie = setCookie(authCookieName, jwtUtils.generateToken(user), authExpiration, "/");
                        this.refreshCookie = setCookie(refreshCookieName, jwtUtils.generateRefreshToken(user),
                                        refreshCookieExpiration,
                                        "/api/v1/public/refresh-token");
                        this.accessCookie = setCookie(accessCookieName, jwtUtils.generateAccessToken(user),
                                        refreshCookieExpiration, "/");
                }
        }
}
