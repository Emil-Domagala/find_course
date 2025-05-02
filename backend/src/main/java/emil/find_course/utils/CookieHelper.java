package emil.find_course.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CookieHelper {

    public static ResponseCookie setCookieHelper(String cookieName, String value, int maxAgeMilis, String path,
            String springProfile, String frontendDomain) {

        return ResponseCookie.from(cookieName, value)
                .httpOnly(true)
                .secure(!"local".equals(
                        springProfile))
                .domain("." + frontendDomain)
                .sameSite("Strict")
                .path(path)
                .maxAge(maxAgeMilis / 100)
                .build();
    }
}
