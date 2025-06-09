package emil.find_course.IntegrationTests.auth;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.Cookie;

import emil.find_course.IntegrationTests.IntegrationTestBase;
import emil.find_course.IntegrationTests.auth.CookieHelperTest.CookieAttributes;
import emil.find_course.IntegrationTests.user.PrepareUserUtil;
import emil.find_course.IntegrationTests.user.UserFactory;
import emil.find_course.common.security.jwt.JwtUtils;
import emil.find_course.user.entity.User;

@Transactional
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
public class AuthControllerRefreshCookieTest extends IntegrationTestBase {
        @Value("${cookie.auth.authToken.name}")
        private String authCookieName;
        @Value("${cookie.auth.refreshToken.name}")
        private String refreshCookieName;

        @Value("${jwt.authToken.expiration}")
        private int authExpiration;

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private PrepareUserUtil prepareUserUtil;

        @Autowired
        private CookieHelperTest cookieHelperTest;

        @Autowired
        private JwtUtils jwtUtils;

        @Test
        @DisplayName("Should refresh auth token when refresh token is valid")
        public void authController_refreshToken_successfullyRefreshesAuthToken() throws Exception {
                User user = prepareUserUtil.prepareVerifiedUser();

                String refreshToken = jwtUtils.generateRefreshToken(user);

                MvcResult result = mockMvc
                                .perform(MockMvcRequestBuilders.post("/api/v1/public/refresh-token")
                                                .cookie(new Cookie(refreshCookieName,
                                                                refreshToken)))
                                .andExpect(MockMvcResultMatchers.status().isOk())
                                .andReturn();

                List<String> setCookies = result.getResponse().getHeaders(HttpHeaders.SET_COOKIE);
                assertThat(setCookies).hasSize(1);

                CookieAttributes cookie = cookieHelperTest.parseSetCookie(setCookies.get(0));

                cookieHelperTest.testCookies(cookie, "/");
        }

        @Test
        @DisplayName("Should return 440 when refresh cookie is not attached")
        public void authController_refreshToken_returns440WhenRefreshCookieIsNotAttached() throws Exception {
                // User user = prepareVerifiedUser();

                MvcResult result = mockMvc
                                .perform(MockMvcRequestBuilders.post("/api/v1/public/refresh-token"))
                                .andExpect(MockMvcResultMatchers.status().is(440))
                                .andReturn();
                List<String> setCookies = result.getResponse().getHeaders(HttpHeaders.SET_COOKIE);
                assertThat(setCookies).hasSize(0);
        }

        @Test
        @DisplayName("Should return 440 when refresh token is expired")
        public void authController_refreshToken_returns440WhenRefreshTokenIsExpired() throws Exception {
                User user = prepareUserUtil.prepareVerifiedUser();

                String refreshToken = jwtUtils.generateExpiredRefreshToken(user);

                MvcResult result = mockMvc
                                .perform(MockMvcRequestBuilders.post("/api/v1/public/refresh-token")
                                                .cookie(new Cookie(refreshCookieName, refreshToken)))
                                .andExpect(MockMvcResultMatchers.status().is(440))
                                .andReturn();

                List<String> setCookies = result.getResponse().getHeaders(HttpHeaders.SET_COOKIE);
                assertThat(setCookies).hasSize(0);
        }

        @Test
        @DisplayName("Should return 440 when refresh token is invalid")
        public void authController_refreshToken_returns440WhenRefreshTokenIsInvalid() throws Exception {

                String refreshToken = "SomeNotValidTokenXDDDD";

                MvcResult result = mockMvc
                                .perform(MockMvcRequestBuilders.post("/api/v1/public/refresh-token")
                                                .cookie(new Cookie(refreshCookieName, refreshToken)))
                                .andExpect(MockMvcResultMatchers.status().is(440))
                                .andReturn();

                List<String> setCookies = result.getResponse().getHeaders(HttpHeaders.SET_COOKIE);
                assertThat(setCookies).hasSize(0);
        }

        @Test
        @DisplayName("Should return 440 when user not found")
        public void authController_refreshToken_returns440WhenUserNotFound() throws Exception {
                User user = UserFactory.createNotVerifiedUser();

                String refreshToken = jwtUtils.generateRefreshToken(user);

                MvcResult result = mockMvc
                                .perform(MockMvcRequestBuilders.post("/api/v1/public/refresh-token")
                                                .cookie(new Cookie(refreshCookieName, refreshToken)))
                                .andExpect(MockMvcResultMatchers.status().is(440))
                                .andReturn();

                List<String> setCookies = result.getResponse().getHeaders(HttpHeaders.SET_COOKIE);
                assertThat(setCookies).hasSize(0);

        }

}
