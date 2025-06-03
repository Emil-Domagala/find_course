package emil.find_course.IntegrationTests.auth;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import emil.find_course.IntegrationTests.IntegrationTestBase;
import emil.find_course.common.security.jwt.JwtUtils;
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
    private UserRepository userRepository;

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
        when(jwtUtils.generateRefreshToken(any(User.class))).thenReturn(mockRefreshToken);

    }

    @Test
    @DisplayName("Should login user sucessfully")
    public void AuthController_login_sucessfullyLoginUser() {

    }

    @ParameterizedTest(name = "Invalid input: email:{0}, password:{1}")
    @DisplayName("Should return 400 for invalid inputs")
    @CsvSource({
            "invalidEmail, Password",
            "test@test.com, "
    })
    void shouldReturn400ForInvalidInputs(String email, String password) throws Exception {

    }

    @Test
    @DisplayName("Should return 400 when email not found")
    public void AuthController_login_returns400WhenEmailNotFound(){}

    @Test
    @DisplayName("Should return 400 when password is incorrect")
    public void AuthController_login_returns400WhenPasswordIsIncorrect(){}


}
