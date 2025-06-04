package emil.find_course.common.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class CookieHelper {

        @Value("${spring.profiles.active}")
        private String springProfile;

        @Value("${domain.name}")
        private String domainName;

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
}
