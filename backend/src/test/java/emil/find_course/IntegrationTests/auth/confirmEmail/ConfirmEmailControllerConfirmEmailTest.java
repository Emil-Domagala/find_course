package emil.find_course.IntegrationTests.auth.confirmEmail;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;

import emil.find_course.IntegrationTests.IntegrationTestBase;
import emil.find_course.IntegrationTests.auth.CookieHelperTest;
import emil.find_course.IntegrationTests.auth.CookieHelperTest.CookieAttributes;
import emil.find_course.IntegrationTests.user.PrepareUserUtil;
import emil.find_course.auth.confirmEmail.dto.request.RequestConfirmEmailOTT;
import emil.find_course.auth.confirmEmail.repository.ConfirmEmailOTTRepository;
import emil.find_course.common.security.jwt.JwtUtils;
import emil.find_course.user.entity.User;
import emil.find_course.user.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.transaction.Transactional;

@Transactional
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
public class ConfirmEmailControllerConfirmEmailTest extends IntegrationTestBase {

        @Value("${cookie.auth.authToken.name}")
        private String authCookieName;

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @Autowired
        private UserRepository userRepository;
        @Autowired
        private ConfirmEmailOTTRepository confirmEmailOTTRepository;

        @Autowired
        private CookieHelperTest cookieHelper;

        @Autowired
        private JwtUtils jwtUtils;

        @Autowired
        private PrepareConfirmEmailOTT prepareConfirmEmailOTT;

        @Autowired
        private PrepareUserUtil prepareUserUtil;

        @Test
        @DisplayName("Should confirm email successfully")
        public void confirmEmailController_confirmEmail_sucessfullyConfirmsEmail() throws Exception {
                User user = prepareUserUtil.prepareNotVerifiedUser();
                String token = prepareConfirmEmailOTT.prepareConfirmEmailOTT(user).getToken();
                RequestConfirmEmailOTT requestConfirmEmailOTT = new RequestConfirmEmailOTT(token);
                String json = objectMapper.writeValueAsString(requestConfirmEmailOTT);
                String authToken = jwtUtils.generateToken(user);

                MvcResult result = mockMvc
                                .perform(MockMvcRequestBuilders.post("/api/v1/confirm-email")
                                                .cookie(new Cookie(authCookieName,
                                                                authToken))
                                                .content(json).contentType(MediaType.APPLICATION_JSON))
                                .andExpect(MockMvcResultMatchers.status().isNoContent())
                                .andReturn();

                assertThat(confirmEmailOTTRepository.findByUser(user)).isEmpty();
                assertThat(userRepository.findByEmail(user.getEmail()).get().isEmailVerified()).isTrue();
                String cookieStr = result.getResponse().getHeader(HttpHeaders.SET_COOKIE);
                CookieAttributes cookie = cookieHelper.parseSetCookie(cookieStr);
                cookieHelper.testCookies(cookie, "/");
        }

        // Invalid passed OTT

        @ParameterizedTest(name = "Invalid passed OTT => token: {0}")
        @CsvSource({ "1", "123456789", "aaaaaa" })
        @DisplayName("Should return 400 when invalid passed OTT")
        public void confirmEmailController_confirmEmail_returns400WhenInvalidPassedOTT(String token) throws Exception {
                User user = prepareUserUtil.prepareNotVerifiedUser();
                prepareConfirmEmailOTT.prepareConfirmEmailOTT(user).getToken();
                RequestConfirmEmailOTT requestConfirmEmailOTT = new RequestConfirmEmailOTT(token);
                String json = objectMapper.writeValueAsString(requestConfirmEmailOTT);
                String authToken = jwtUtils.generateToken(user);

                mockMvc
                                .perform(MockMvcRequestBuilders.post("/api/v1/confirm-email")
                                                .cookie(new Cookie(authCookieName,
                                                                authToken))
                                                .content(json).contentType(MediaType.APPLICATION_JSON))
                                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                                .andReturn();

        }

        @Test
        @DisplayName("Should return 404 when didnt found OTT")
        public void confirmEmailController_confirmEmail_returns404WhenDidntFoundOTT() throws Exception {
                User user = prepareUserUtil.prepareNotVerifiedUser();
                RequestConfirmEmailOTT requestConfirmEmailOTT = new RequestConfirmEmailOTT("aaaaaa");
                String json = objectMapper.writeValueAsString(requestConfirmEmailOTT);
                String authToken = jwtUtils.generateToken(user);

                mockMvc
                                .perform(MockMvcRequestBuilders.post("/api/v1/confirm-email")
                                                .cookie(new Cookie(authCookieName,
                                                                authToken))
                                                .content(json).contentType(MediaType.APPLICATION_JSON))
                                .andExpect(MockMvcResultMatchers.status().isNotFound())
                                .andReturn();

        }

        // OTT expired
        @Test
        @DisplayName("Should return 400 when OTT Expired")
        public void confirmEmailController_confirmEmail_returns400WhenOTTExpired() throws Exception {
                User user = prepareUserUtil.prepareNotVerifiedUser();
                String token = prepareConfirmEmailOTT.prepareExpiredConfirmEmailOTT(user).getToken();
                RequestConfirmEmailOTT requestConfirmEmailOTT = new RequestConfirmEmailOTT(token);
                String json = objectMapper.writeValueAsString(requestConfirmEmailOTT);
                String authToken = jwtUtils.generateToken(user);

                mockMvc
                                .perform(MockMvcRequestBuilders.post("/api/v1/confirm-email")
                                                .cookie(new Cookie(authCookieName,
                                                                authToken))
                                                .content(json).contentType(MediaType.APPLICATION_JSON))
                                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                                .andReturn();

        }

        // Didnt found OTT
        @Test
        @DisplayName("Should return 404 when token not found")
        public void confirmEmailController_confirmEmail_returns404WhenOTTNotFound() throws Exception {
                User userToken = prepareUserUtil.prepareNotVerifiedUser(UUID.randomUUID() + "@example.com", "John");
                User userLogin = prepareUserUtil.prepareNotVerifiedUser();
                String token = prepareConfirmEmailOTT.prepareExpiredConfirmEmailOTT(userToken).getToken();
                RequestConfirmEmailOTT requestConfirmEmailOTT = new RequestConfirmEmailOTT(token);
                String json = objectMapper.writeValueAsString(requestConfirmEmailOTT);
                String authToken = jwtUtils.generateToken(userLogin);

                mockMvc
                                .perform(MockMvcRequestBuilders.post("/api/v1/confirm-email")
                                                .cookie(new Cookie(authCookieName,
                                                                authToken))
                                                .content(json).contentType(MediaType.APPLICATION_JSON))
                                .andExpect(MockMvcResultMatchers.status().isNotFound())
                                .andReturn();

        }

        // Verified user tries to verifie again
        @Test
        @DisplayName("Should return 400 when verified user tries to verifie again")
        public void confirmEmailController_confirmEmail_returns400WhenVerifiedUserTriesToVerifieAgain()
                        throws Exception {
                User user = prepareUserUtil.prepareVerifiedUser();
                String token = prepareConfirmEmailOTT.prepareConfirmEmailOTT(user).getToken();
                RequestConfirmEmailOTT requestConfirmEmailOTT = new RequestConfirmEmailOTT(token);
                String json = objectMapper.writeValueAsString(requestConfirmEmailOTT);
                String authToken = jwtUtils.generateToken(user);

                mockMvc
                                .perform(MockMvcRequestBuilders.post("/api/v1/confirm-email")
                                                .cookie(new Cookie(authCookieName,
                                                                authToken))
                                                .content(json).contentType(MediaType.APPLICATION_JSON))
                                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                                .andReturn();
        }

        // No cookie or bad token
        @Test
        @DisplayName("Should return 498 when no cookie set or bad token passed")
        public void confirmEmailController_confirmEmail_returns498WhenNoCookieSetOrBadTokenPassed() throws Exception {
                User user = prepareUserUtil.prepareNotVerifiedUser();
                String token = prepareConfirmEmailOTT.prepareConfirmEmailOTT(user).getToken();
                RequestConfirmEmailOTT requestConfirmEmailOTT = new RequestConfirmEmailOTT(token);
                String json = objectMapper.writeValueAsString(requestConfirmEmailOTT);

                mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/confirm-email").cookie(new Cookie(authCookieName,
                                "Bad token")).content(json).contentType(MediaType.APPLICATION_JSON))
                                .andExpect(MockMvcResultMatchers.status().is(498));

        }
}
