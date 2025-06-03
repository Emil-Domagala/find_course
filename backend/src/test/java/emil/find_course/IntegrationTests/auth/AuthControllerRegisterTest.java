package emil.find_course.IntegrationTests.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;

import emil.find_course.TestDataUtil;
import emil.find_course.IntegrationTests.IntegrationTestBase;
import emil.find_course.auth.dto.request.UserRegisterRequest;
import emil.find_course.auth.email.EmailVerificationService;
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

        @MockBean
        private JwtUtils jwtUtils;

        @MockBean
        private EmailVerificationService emailVerificationService;

        private UserRegisterRequest validUserRegisterRequest;
        private String mockAuthToken;
        private String mockRefreshToken;

        @BeforeEach
        void setUp() {
                validUserRegisterRequest = AuthControllerUtils.createUserRegisterRequest(
                                "testregister@example.com", "testUserReg", "TestLastNameReg", "Password123!");

                mockAuthToken = "mockAuthTokenForRegister";
                mockRefreshToken = "mockRefreshTokenForRegister";

                // Configure mocks
                when(jwtUtils.generateToken(any(User.class))).thenReturn(mockAuthToken);
                when(jwtUtils.generateRefreshToken(any(User.class))).thenReturn(mockRefreshToken);
                doNothing().when(emailVerificationService).sendVerificationEmail(any(User.class));
        }

        @Test
        public void AuthController_Register_SucessfullyPasses() throws Exception {
                UserRegisterRequest userRegisterRequest = AuthControllerUtils.createUserRegisterRequest();
                String userRegisterRequestJson = objectMapper.writeValueAsString(userRegisterRequest);
                mockMvc.perform(
                                MockMvcRequestBuilders.post("/api/v1/public/register")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(userRegisterRequestJson))
                                .andExpect(MockMvcResultMatchers.status().isOk());

                Optional<User> savedUser = userRepository.findByEmail(userRegisterRequest.getEmail());

                assertThat(savedUser.isPresent());
                assertThat(savedUser.get().getUsername()).isEqualTo(userRegisterRequest.getUsername());
                assertThat(savedUser.get().getUserLastname()).isEqualTo(userRegisterRequest.getUserLastname());
        }

        @Test
        public void AuthController_Register_Returns400WhenBadEmail() throws Exception {
                UserRegisterRequest userRegisterRequestInvalid = AuthControllerUtils.createUserRegisterRequest("test",
                                "John",
                                "Doe", "Password");
                String userRegisterRequestJson = objectMapper.writeValueAsString(userRegisterRequestInvalid);
                mockMvc.perform(
                                MockMvcRequestBuilders.post("/api/v1/public/register")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(userRegisterRequestJson))
                                .andExpect(MockMvcResultMatchers.status().isBadRequest());
                Optional<User> invalidUser = userRepository.findByEmail(userRegisterRequestInvalid.getEmail());
                assertThat(invalidUser.isEmpty());
        }

        @Test
        public void AuthController_Register_Returns400WhenBadPassword() throws Exception {
                UserRegisterRequest userRegisterRequestInvalid = AuthControllerUtils.createUserRegisterRequest(
                                "test@test.com",
                                "John",
                                "Doe", "");
                String userRegisterRequestJson = objectMapper.writeValueAsString(userRegisterRequestInvalid);
                mockMvc.perform(
                                MockMvcRequestBuilders.post("/api/v1/public/register")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(userRegisterRequestJson))
                                .andExpect(MockMvcResultMatchers.status().isBadRequest());

                Optional<User> invalidUser = userRepository.findByEmail(userRegisterRequestInvalid.getEmail());
                assertThat(invalidUser.isEmpty());
        }

        @Test
        public void AuthController_Register_Returns400WhenBadName() throws Exception {
                UserRegisterRequest userRegisterRequestInvalid = AuthControllerUtils.createUserRegisterRequest(
                                "test@test.com",
                                "",
                                "Doe", "Password");
                String userRegisterRequestJson = objectMapper.writeValueAsString(userRegisterRequestInvalid);
                mockMvc.perform(
                                MockMvcRequestBuilders.post("/api/v1/public/register")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(userRegisterRequestJson))
                                .andExpect(MockMvcResultMatchers.status().isBadRequest());

                Optional<User> invalidUser = userRepository.findByEmail(userRegisterRequestInvalid.getEmail());
                assertThat(invalidUser.isEmpty());
        }

        @Test
        public void AuthController_Register_Returns400WhenBadLastname() throws Exception {
                UserRegisterRequest userRegisterRequestInvalid = AuthControllerUtils.createUserRegisterRequest(
                                "test@test.com",
                                "John",
                                "", "Password");
                String userRegisterRequestJson = objectMapper.writeValueAsString(userRegisterRequestInvalid);
                mockMvc.perform(
                                MockMvcRequestBuilders.post("/api/v1/public/register")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(userRegisterRequestJson))
                                .andExpect(MockMvcResultMatchers.status().isBadRequest());
                Optional<User> invalidUser = userRepository.findByEmail(userRegisterRequestInvalid.getEmail());
                assertThat(invalidUser.isEmpty());
        }

        @Test
        public void AuthController_Register_Returns400WhenEmailAlreadyExists() throws Exception {
                UserRegisterRequest userRegisterRequestInvalid = AuthControllerUtils
                                .createUserRegisterRequest("test@test.com");
                User user = TestDataUtil.createVerifiedUser("test@test.com", "John");
                userRepository.save(user);

                Optional<User> savedUser = userRepository.findByEmail(user.getEmail());
                assertThat(savedUser.isPresent());

                String userRegisterRequestJson = objectMapper.writeValueAsString(userRegisterRequestInvalid);
                mockMvc.perform(
                                MockMvcRequestBuilders.post("/api/v1/public/register")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(userRegisterRequestJson))
                                .andExpect(MockMvcResultMatchers.status().isBadRequest());
        }

}
