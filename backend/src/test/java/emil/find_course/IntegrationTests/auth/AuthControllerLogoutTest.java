package emil.find_course.IntegrationTests.auth;

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
import emil.find_course.IntegrationTests.auth.CookieHelperTest.CookieAttributes;

@Transactional
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
public class AuthControllerLogoutTest extends IntegrationTestBase {

    @Value("${cookie.auth.authToken.name}")
    private String authCookieName;
    @Value("${cookie.auth.refreshToken.name}")
    private String refreshCookieName;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CookieHelperTest cookieHelper;

    @Test
    @DisplayName("Should sucessfully reset cookies")
    public void authController_logout_sucessfullyResetCookies() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/public/logout"))
                .andExpect(MockMvcResultMatchers.status().isNoContent()).andReturn();

        List<String> setCookies = result.getResponse().getHeaders(HttpHeaders.SET_COOKIE);
        assertThat(setCookies).hasSize(3);

        Map<String, CookieAttributes> cookies = setCookies.stream().map(cookieHelper::parseSetCookie)
                .collect(Collectors.toMap(CookieAttributes::getName, ca -> ca));

        cookieHelper.testCookies(cookies.get(authCookieName), "", "/", 0);
        cookieHelper.testCookies(cookies.get(refreshCookieName), "", "/api/v1/public/refresh-token", 0);
    }

}
