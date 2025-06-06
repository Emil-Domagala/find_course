package emil.find_course.IntegrationTests.user;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import emil.find_course.IntegrationTests.IntegrationTestBase;
import emil.find_course.common.security.jwt.JwtUtils;
import emil.find_course.user.dto.UserDto;
import emil.find_course.user.entity.User;
import jakarta.servlet.http.Cookie;

@Transactional
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
public class UserControllerGetUserInfoTest extends IntegrationTestBase {

    @Value("${cookie.auth.authToken.name}")
    private String authCookieName;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PrepareUserUtil prepareUserUtil;

    // No cookie throws 403
    @Test
    @DisplayName("Should return 403 when no cookie set")
    public void userController_getUserInfo_returns403WhenNoCookieSet() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.get("/api/v1/user")).andExpect(MockMvcResultMatchers.status().isForbidden())
                .andReturn();
    }

    // Wrong token throws 403
    @Test
    @DisplayName("Should return 403 when bad token passed")
    public void userController_getUserInfo_returns403WhenNBadTokenPassed() throws Exception {
        // User user = prepareUser();

        mockMvc.perform(
                MockMvcRequestBuilders.get("/api/v1/user").cookie(new Cookie(authCookieName, "badToken")))
                .andExpect(MockMvcResultMatchers.status().isForbidden()).andReturn();
    }

    // Returns UserDto
    @Test
    @DisplayName("Should return UserDto")
    public void userController_getUserInfo_returnsUserDto() throws Exception {
        User user = prepareUserUtil.prepareVerifiedUser();
        String authToken = jwtUtils.generateToken(user);

        MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/v1/user").cookie(new Cookie(authCookieName, authToken)))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

        UserDto userDto = objectMapper.readValue(result.getResponse().getContentAsString(), UserDto.class);

        assertThat(userDto.getUsername()).isEqualTo(user.getUsername());
        assertThat(userDto.getUserLastname()).isEqualTo(user.getUserLastname());
        assertThat(userDto.getEmail()).isEqualTo(user.getEmail());
        assertThat(userDto.getImageUrl()).isEqualTo(user.getImageUrl());
    }
}
