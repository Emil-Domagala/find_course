package emil.find_course.IntegrationTests.user;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

import emil.find_course.IntegrationTests.IntegrationTestBase;
import emil.find_course.IntegrationTests.auth.CookieHelperTest;
import emil.find_course.IntegrationTests.auth.CookieHelperTest.CookieAttributes;
import emil.find_course.common.security.jwt.JwtUtils;
import emil.find_course.user.entity.User;
import emil.find_course.user.repository.UserRepository;
import jakarta.servlet.http.Cookie;

@Transactional
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
public class UserControllerDeleteUserTest extends IntegrationTestBase {

    @Value("${cookie.auth.authToken.name}")
    private String authCookieName;
    @Value("${cookie.auth.refreshToken.name}")
    private String refreshCookieName;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PrepareUserUtil prepareUserUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CookieHelperTest cookieHelper;

    private void assertAuthAndRefreshCookies(MvcResult result) {
        List<String> setCookies = result.getResponse().getHeaders(HttpHeaders.SET_COOKIE);
        assertThat(setCookies).hasSize(2);

        Map<String, CookieAttributes> cookies = setCookies.stream()
                .map(cookieHelper::parseSetCookie)
                .collect(Collectors.toMap(CookieAttributes::getName, ca -> ca));

        // Assert auth token cookie
        cookieHelper.testCookies(cookies.get(authCookieName), "", "/", 0);

        // Assert refresh token cookie
        cookieHelper.testCookies(cookies.get(refreshCookieName), "",
                "/api/v1/public/refresh-token", 0);
    }

    // Sucessfully deletes user

    @Test
    @DisplayName("Should delete user")
    public void userController_deleteUser_sucessfullyDeleteUser() throws Exception {
        User user = prepareUserUtil.prepareVerifiedUser();
        String authToken = jwtUtils.generateToken(user);

        MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.delete("/api/v1/user").cookie(new Cookie(authCookieName, authToken)))
                .andExpect(MockMvcResultMatchers.status().isNoContent()).andReturn();

        assertAuthAndRefreshCookies(result);
        assertThat(userRepository.findByEmail(user.getEmail())).isEmpty();

    }

}
