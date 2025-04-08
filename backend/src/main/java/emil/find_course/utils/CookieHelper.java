package emil.find_course.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CookieHelper {

    @Value("${frontend.domain}")
    private static String frontendDomain;

    public static ResponseCookie setCookieHelper(String cookieName, String value, int maxAge, String path) {
        return ResponseCookie.from(cookieName, value)
                .httpOnly(true)
                .secure(true)
                .domain(frontendDomain)
                .sameSite("Strict")
                .path(path)
                .maxAge(maxAge / 1)
                .build();
    }
}
