package emil.find_course.IntegrationTests.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import static org.mockito.ArgumentMatchers.any;

import com.fasterxml.jackson.databind.ObjectMapper;

import emil.find_course.IntegrationTests.IntegrationTestBase;
import emil.find_course.IntegrationTests.auth.CookieHelperTest.CookieAttributes;
import emil.find_course.IntegrationTests.user.PrepareUserUtil;
import emil.find_course.IntegrationTests.user.UserFactory;
import emil.find_course.auth.confirmEmail.ConfirmEmailService;
import emil.find_course.auth.dto.request.UserRegisterRequest;
import emil.find_course.common.security.jwt.JwtUtils;
import emil.find_course.user.entity.User;
import emil.find_course.user.repository.UserRepository;
import jakarta.transaction.Transactional;

@Transactional
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
public class AuthControllerRegisterTest extends IntegrationTestBase {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private CookieHelperTest cookieHelper;

        @Autowired
        private PrepareUserUtil prepareUserUtil;

        @MockitoBean
        private JwtUtils jwtUtils;

        @MockitoBean
        private ConfirmEmailService confirmEmailService;

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
                when(jwtUtils.generateRefreshToken(any(User.class))).thenReturn(mockRefreshToken);
                doNothing().when(confirmEmailService).sendVerificationEmail(any(User.class));
        }

        @Test
        @DisplayName("Should register user successfully")
        public void authController_Register_SucessfullyPasses() throws Exception {
                UserRegisterRequest userRegisterRequest = AuthControllerUtils.createUserRegisterRequest();
                String userRegisterRequestJson = objectMapper.writeValueAsString(userRegisterRequest);

                MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/public/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(userRegisterRequestJson))
                                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

                List<String> setCookies = result.getResponse().getHeaders(HttpHeaders.SET_COOKIE);
                assertThat(setCookies).hasSize(3);

                Map<String, CookieAttributes> cookies = setCookies.stream()
                                .map(cookieHelper::parseSetCookie)
                                .collect(Collectors.toMap(CookieAttributes::getName, ca -> ca));

                // Assert auth token cookie
                cookieHelper.testCookies(cookies.get(authCookieName), mockAuthToken, "/");

                // Assert refresh token cookie
                cookieHelper.testCookies(cookies.get(refreshCookieName), mockRefreshToken,
                                "/api/v1/public/refresh-token");

                Optional<User> savedUser = userRepository.findByEmail(userRegisterRequest.getEmail());

                assertThat(savedUser.isPresent());
                assertThat(savedUser.get().getUsername()).isEqualTo(userRegisterRequest.getUsername());
                assertThat(savedUser.get().getUserLastname()).isEqualTo(userRegisterRequest.getUserLastname());
                verify(confirmEmailService).sendVerificationEmail(any(User.class));
                verify(jwtUtils).generateToken(any(User.class));
                verify(jwtUtils).generateRefreshToken(any(User.class));
        }

        @ParameterizedTest(name = "Invalid input => email: {0}, name: {1}, lastname: {2}, password: {3}")
        @DisplayName("Should return 400 for invalid inputs")
        @CsvSource({
                        "invalidEmail, John, Doe, Password",
                        "test@test.com, , Doe, Password",
                        "test@test.com, John, , Password",
                        "test@test.com, John, Doe, "
        })
        void authController_Register_Returns400WhenInvalidInputs(String email, String name, String lastname,
                        String password)
                        throws Exception {
                UserRegisterRequest userRegisterRequest = AuthControllerUtils.createUserRegisterRequest(email, name,
                                lastname, password);
                String json = objectMapper.writeValueAsString(userRegisterRequest);

                mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/public/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                                .andExpect(MockMvcResultMatchers.status().isBadRequest());

                assertThat(userRepository.findByEmail(email)).isEmpty();
        }

        @Test
        @DisplayName("Should return 400 when email already exists")
        public void authController_Register_Returns400WhenEmailAlreadyExists() throws Exception {
                UserRegisterRequest userRegisterRequestInvalid = AuthControllerUtils
                                .createUserRegisterRequest(UserFactory.BASE_EMAIL);

                prepareUserUtil.prepareVerifiedUser();

                String userRegisterRequestJson = objectMapper.writeValueAsString(userRegisterRequestInvalid);
                mockMvc.perform(
                                MockMvcRequestBuilders.post("/api/v1/public/register")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(userRegisterRequestJson))
                                .andExpect(MockMvcResultMatchers.status().isBadRequest());
        }
}
