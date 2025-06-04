package emil.find_course.IntegrationTests.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import emil.find_course.TestDataUtil;
import emil.find_course.IntegrationTests.IntegrationTestBase;
import emil.find_course.IntegrationTests.auth.CookieHelperTest.CookieAttributes;
import emil.find_course.auth.dto.request.UserLoginRequest;
import emil.find_course.common.security.jwt.JwtUtils;
import emil.find_course.common.security.jwt.UserDetailsImpl;
import emil.find_course.user.entity.User;
import emil.find_course.user.repository.UserRepository;

@Transactional
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
public class AuthControllerLoginTest extends IntegrationTestBase {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @Autowired
        private PasswordEncoder passwordEncoder;

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private CookieHelperTest cookieHelper;

        @MockitoBean
        private JwtUtils jwtUtils;

        @Value("${cookie.auth.authToken.name}")
        private String authCookieName;
        @Value("${cookie.auth.refreshToken.name}")
        private String refreshCookieName;

        private String mockAuthToken;
        private String mockRefreshToken;

        @BeforeEach
        void setUp() {
                mockAuthToken = "mockAuthTokenForRegister";
                mockRefreshToken = "mockRefreshTokenForRegister";

                // Configure mocks
                when(jwtUtils.generateToken(any(User.class))).thenReturn(mockAuthToken);
                when(jwtUtils.generateToken(any(UserDetailsImpl.class))).thenReturn(mockAuthToken);
                when(jwtUtils.generateRefreshToken(any(User.class))).thenReturn(mockRefreshToken);

        }

        private Map<String, String> saveUser() {
                User user = TestDataUtil.createVerifiedUser();
                String decodedPassword = user.getPassword();
                user.setPassword(passwordEncoder.encode(user.getPassword()));
                userRepository.save(user);

                assertThat(userRepository.findByEmail(user.getEmail()).isPresent());
                return Map.of("email", user.getEmail(), "password", decodedPassword);
        }

        @Test
        @DisplayName("Should login user sucessfully")
        public void authController_login_sucessfullyLoginUser() throws Exception {
                Map<String, String> userMap = saveUser();
                String email = userMap.get("email");
                String decodedPassword = userMap.get("password");

                UserLoginRequest userLoginRequest = AuthControllerUtils.createUserLoginRequest(email,
                                decodedPassword);
                String json = objectMapper.writeValueAsString(userLoginRequest);

                MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/public/login")
                                .contentType(MediaType.APPLICATION_JSON).content(json))
                                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

                List<String> setCookies = result.getResponse().getHeaders(HttpHeaders.SET_COOKIE);
                assertThat(setCookies).hasSize(2);

                Map<String, CookieAttributes> cookies = setCookies.stream()
                                .map(cookieHelper::parseSetCookie)
                                .collect(Collectors.toMap(CookieAttributes::getName, ca -> ca));

                // Assert auth token cookie
                cookieHelper.testCookies(cookies.get(authCookieName), mockAuthToken, "/");

                // Assert refresh token cookie
                cookieHelper.testCookies(cookies.get(refreshCookieName), mockRefreshToken,
                                "/api/v1/public/refresh-token");

                verify(jwtUtils).generateToken(any(UserDetailsImpl.class));
                verify(jwtUtils).generateRefreshToken(any(User.class));

        }

        @ParameterizedTest(name = "Invalid input: email:{0}, password:{1}")
        @DisplayName("Should return 400 for invalid inputs")
        @CsvSource({
                        "invalidEmail, Password",
                        "test@test.com, "
        })
        void authController_login_return400ForInvalidInputs(String email, String password) throws Exception {
                UserLoginRequest userLoginRequest = AuthControllerUtils.createUserLoginRequest(email, password);
                String json = objectMapper.writeValueAsString(userLoginRequest);

                mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/public/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                                .andExpect(MockMvcResultMatchers.status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 401 when email not found")
        public void authController_login_returns401WhenEmailNotFound() throws Exception {
                Map<String, String> userMap = saveUser();
                String decodedPassword = userMap.get("password");

                UserLoginRequest userLoginRequest = AuthControllerUtils.createUserLoginRequest(
                                UUID.randomUUID() + "@example.com",
                                decodedPassword);
                String json = objectMapper.writeValueAsString(userLoginRequest);

                mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/public/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
        }

        @Test
        @DisplayName("Should return 401 when password is incorrect")
        public void authController_login_returns401WhenPasswordIsIncorrect() throws Exception {
                Map<String, String> userMap = saveUser();
                String email = userMap.get("email");
                UserLoginRequest userLoginRequest = AuthControllerUtils.createUserLoginRequest(email,
                                "incorrectPassword");
                String json = objectMapper.writeValueAsString(userLoginRequest);

                mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/public/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
        }

}
