package emil.find_course.IntegrationTests.auth;

import static org.assertj.core.api.Assertions.assertThat;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Component
public class CookieHelperTest {

    @Value("${spring.profiles.active}")
    private String springProfile;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public class CookieAttributes {
        private String name;
        private String value;
        private String path;
        private String domain;
        private boolean httpOnly;
        private boolean secure;
        private String sameSite;
        private int maxAge;
    }

    public CookieAttributes parseSetCookie(String setCookieHeader) {
        CookieAttributes cookie = new CookieAttributes();
        String[] parts = setCookieHeader.split(";");

        String[] nameValue = parts[0].split("=", 2);
        cookie.name = nameValue[0].trim();
        cookie.value = nameValue.length > 1 ? nameValue[1].trim() : "";

        for (int i = 1; i < parts.length; i++) {
            String part = parts[i].trim();
            if (part.equalsIgnoreCase("HttpOnly"))
                cookie.setHttpOnly(true);
            else if (part.equalsIgnoreCase("Secure"))
                cookie.setSecure(true);
            else if (part.toLowerCase().startsWith("path="))
                cookie.setPath(part.substring(5).trim());
            else if (part.toLowerCase().startsWith("domain="))
                cookie.setDomain(part.substring(7).trim());
            else if (part.toLowerCase().startsWith("samesite="))
                cookie.setSameSite(part.substring(9).trim());
            else if (part.toLowerCase().startsWith("max-age="))
                cookie.setMaxAge(Integer.parseInt(part.substring(8).trim()));
        }

        return cookie;
    }

    public void testCookies(CookieAttributes cookie, String expectedValue, String expectedPath) {
        assertThat(cookie).isNotNull();
        assertThat(cookie.getValue()).isEqualTo(expectedValue);
        assertThat(cookie.isHttpOnly()).isTrue();
        if ("local".equals(springProfile)) {
            assertThat(cookie.isSecure()).isFalse();
        } else {
            assertThat(cookie.isSecure()).isTrue();
        }
        assertThat(cookie.getPath()).isEqualTo(expectedPath);
        assertThat(cookie.getSameSite()).isEqualToIgnoringCase("Strict");
    }

    public void testCookies(CookieAttributes cookie, String expectedValue, String expectedPath, Integer maxAge) {
        assertThat(cookie).isNotNull();
        assertThat(cookie.getValue()).isEqualTo(expectedValue);
        assertThat(cookie.isHttpOnly()).isTrue();
        if ("local".equals(springProfile)) {
            assertThat(cookie.isSecure()).isFalse();
        } else {
            assertThat(cookie.isSecure()).isTrue();
        }
        assertThat(cookie.getPath()).isEqualTo(expectedPath);
        assertThat(cookie.getSameSite()).isEqualToIgnoringCase("Strict");
        assertThat(cookie.getMaxAge()).isEqualTo(maxAge);
    }

    public void testCookies(CookieAttributes cookie, String expectedPath) {
        assertThat(cookie).isNotNull();
        assertThat(cookie.getValue()).isNotEmpty();
        assertThat(cookie.isHttpOnly()).isTrue();
        if ("local".equals(springProfile)) {
            assertThat(cookie.isSecure()).isFalse();
        } else {
            assertThat(cookie.isSecure()).isTrue();
        }
        assertThat(cookie.getPath()).isEqualTo(expectedPath);
        assertThat(cookie.getSameSite()).isEqualToIgnoringCase("Strict");
    }

}
