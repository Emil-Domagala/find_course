package emil.find_course.IntegrationTests.user;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import emil.find_course.TestDataUtil;
import emil.find_course.IntegrationTests.IntegrationTestBase;
import emil.find_course.IntegrationTests.auth.CookieHelperTest;
import emil.find_course.user.dto.request.RequestUpdateUser;
import emil.find_course.user.entity.User;
import emil.find_course.user.repository.UserRepository;

@Transactional
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
public class UserControllerUpdateUserInfoTest extends IntegrationTestBase {

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

    // Create setup where i will mock fileStorageService

    private User saveUser() {
        User user = TestDataUtil.createVerifiedUser();
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        assertThat(userRepository.findByEmail(user.getEmail()).isPresent());
        return user;

    }

    @ParameterizedTest(name = "Valid input => username: {0}, userLastname: {1}, password: {2}")
    @DisplayName("Should update user info without image")
    @CsvSource({
            "JohnNewName, DoeNewLastname,  PasswordNew",
            "JohnNewName, DoeNewLastname,  "
    })
    public void userController_updateUserInfo_sucessfullyUpdateUserInfo(String username, String userLastname,
            String password) throws Exception {
        User user = saveUser();

        RequestUpdateUser requestUpdateUser = RequestUpdateUser.builder().username("New Name")
                .userLastname("New Lastname").build();

    }

    @ParameterizedTest(name = "Valid input => username: {0}, userLastname: {1}, password: {2}")
    @DisplayName("Shouldn't update password if invalid")
    @CsvSource({
            "John, Doe,  P",
            "John, Doe,  1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,Password",
    })
    public void userController_updateUserInfo_doNotUpdatePasswordIfInvalid() throws Exception {
        User user = saveUser();

        RequestUpdateUser requestUpdateUser = RequestUpdateUser.builder().username("New Name")
                .userLastname("New Lastname").build();

    }

    @Test
    @DisplayName("Should delete user image")
    public void userController_updateUserInfo_deleteImage() throws Exception {
        User user = saveUser();
    }

    @Test
    @DisplayName("Should update user image")
    public void userController_updateUserInfo_updateImage() throws Exception {
        User user = saveUser();
    }

}
