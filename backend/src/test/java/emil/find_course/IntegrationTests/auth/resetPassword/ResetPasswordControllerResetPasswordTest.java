package emil.find_course.IntegrationTests.auth.resetPassword;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;

import emil.find_course.IntegrationTests.IntegrationTestBase;
import emil.find_course.IntegrationTests.user.PrepareUserUtil;
import emil.find_course.IntegrationTests.user.UserFactory;
import emil.find_course.auth.resetPassword.dto.request.ResetPasswordPasswordRequest;
import emil.find_course.auth.resetPassword.entity.ResetPasswordOTT;
import emil.find_course.auth.resetPassword.repository.ResetPasswordOTTRepository;
import emil.find_course.common.service.EmailService;
import emil.find_course.user.entity.User;
import jakarta.transaction.Transactional;

@Transactional
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
public class ResetPasswordControllerResetPasswordTest extends IntegrationTestBase {

    private final String NEW_PASSWORD = "New!Pass1";

    @Value("${cookie.auth.authToken.name}")
    private String authCookieName;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PrepareUserUtil prepareUserUtil;

    @Autowired
    private PrepareResetPasswordOTT prepareResetPasswordOTT;

    @Autowired
    private ResetPasswordOTTRepository resetPasswordOTTRepository;

    @MockitoBean
    private EmailService emailService;

    @Test
    @DisplayName("should sucessfully change user password")
    public void resetPasswordController_ResetPassword_SucessfullyResetPassword() throws Exception {
        User user = prepareUserUtil.prepareVerifiedUser();
        ResetPasswordOTT resetPasswordOTT = prepareResetPasswordOTT.prepareResetPasswordOTT(user);
        String token = resetPasswordOTT.getToken();
        ResetPasswordPasswordRequest request = new ResetPasswordPasswordRequest(NEW_PASSWORD);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/public/reset-password?token=" + token)
                .content(objectMapper.writeValueAsString(request)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        assertThat(UserFactory.BASE_PASSWORD != NEW_PASSWORD).isTrue();
        assertThat(passwordEncoder.matches(NEW_PASSWORD, user.getPassword())).isTrue();
        assertThat(resetPasswordOTTRepository.findByToken(token)).isEmpty();
    }

    @ParameterizedTest(name = "Not found OOT: {0}")
    @CsvSource({
            "123456",
            "1",
            "1234567"
    })
    @DisplayName("should return 404 when resetPasswordOTT not found")
    public void resetPasswordController_ResetPassword_Returns404WhenOOTnotFound(String token) throws Exception {
        ResetPasswordPasswordRequest request = new ResetPasswordPasswordRequest(NEW_PASSWORD);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/public/reset-password?token=" + token)
                .content(objectMapper.writeValueAsString(request)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

    }

    @Test
    @DisplayName("should return 404 when resetPasswordOTT not included")
    public void resetPasswordController_ResetPassword_Returns404WhenOOTnotIncluded() throws Exception {
        ResetPasswordPasswordRequest request = new ResetPasswordPasswordRequest(NEW_PASSWORD);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/public/reset-password?token=")
                .content(objectMapper.writeValueAsString(request)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

    }

    @Test
    @DisplayName("should return 404 when resetPasswordOTT not included")
    public void resetPasswordController_ResetPassword_Returns400WhenOOTRequestParamNotIncluded() throws Exception {
        ResetPasswordPasswordRequest request = new ResetPasswordPasswordRequest(NEW_PASSWORD);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/public/reset-password")
                .content(objectMapper.writeValueAsString(request)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

    }

    // OOT Expired
    @Test
    @DisplayName("should return 400 when resetPasswordOTT expired")
    public void resetPasswordController_ResetPassword_Returns400WhenOOTExpired() throws Exception {
        User user = prepareUserUtil.prepareVerifiedUser();
        var resetPasswordOTT = prepareResetPasswordOTT.prepareExpiredResetPasswordOTT(user);
        String token = resetPasswordOTT.getToken();
        ResetPasswordPasswordRequest request = new ResetPasswordPasswordRequest(NEW_PASSWORD);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/public/reset-password?token=" + token)
                .content(objectMapper.writeValueAsString(request)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

    }

    // User not verified
    @Test
    @DisplayName("should return 400 when user not verivied")
    public void resetPasswordController_ResetPassword_Returns400WhenUserNotVerified() throws Exception {
        User user = prepareUserUtil.prepareNotVerifiedUser();
        var resetPasswordOTT = prepareResetPasswordOTT.prepareExpiredResetPasswordOTT(user);
        String token = resetPasswordOTT.getToken();
        ResetPasswordPasswordRequest request = new ResetPasswordPasswordRequest(NEW_PASSWORD);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/public/reset-password?token=" + token)
                .content(objectMapper.writeValueAsString(request)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

    }

    // Old password cant be this same as old one
    @Test
    @DisplayName("should return 400 when old password == new password")
    public void resetPasswordController_ResetPassword_Returns400WhenNewPasswordEqualsOld() throws Exception {
        User user = prepareUserUtil.prepareVerifiedUser();
        var resetPasswordOTT = prepareResetPasswordOTT.prepareExpiredResetPasswordOTT(user);
        String token = resetPasswordOTT.getToken();
        ResetPasswordPasswordRequest request = new ResetPasswordPasswordRequest(UserFactory.BASE_PASSWORD);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/public/reset-password?token=" + token)
                .content(objectMapper.writeValueAsString(request)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    // Invalid password should be rejected (to short, long)
    @ParameterizedTest(name = "Invalid password: {0}")
    @CsvSource({ "P", "1-2-3-4-5-6-7-8-9-10-11" })
    public void resetPasswordController_ResetPassword_Returns400WhenNewPasswordInvalid(String password)
            throws Exception {
        User user = prepareUserUtil.prepareVerifiedUser();
        var resetPasswordOTT = prepareResetPasswordOTT.prepareExpiredResetPasswordOTT(user);
        String token = resetPasswordOTT.getToken();
        ResetPasswordPasswordRequest request = new ResetPasswordPasswordRequest(password);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/public/reset-password?token=" + token)
                .content(objectMapper.writeValueAsString(request)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @DisplayName("should return 400 when new password null")
    public void resetPasswordController_ResetPassword_Returns400WhenNewPasswordIsNull() throws Exception {
        User user = prepareUserUtil.prepareVerifiedUser();
        var resetPasswordOTT = prepareResetPasswordOTT.prepareExpiredResetPasswordOTT(user);
        String token = resetPasswordOTT.getToken();
        ResetPasswordPasswordRequest request = new ResetPasswordPasswordRequest(null);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/public/reset-password?token=" + token)
                .content(objectMapper.writeValueAsString(request)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}
