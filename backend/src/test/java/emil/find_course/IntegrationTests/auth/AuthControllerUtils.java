package emil.find_course.IntegrationTests.auth;

import emil.find_course.auth.dto.request.UserLoginRequest;
import emil.find_course.auth.dto.request.UserRegisterRequest;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class AuthControllerUtils {

    public static UserLoginRequest createUserLoginRequest() {
        return UserLoginRequest.builder().email("test@test.com").password("Password").build();
    }

    public static UserLoginRequest createUserLoginRequest(String email, String password) {
        return UserLoginRequest.builder().email(email).password(password).build();
    }

    public static UserRegisterRequest createUserRegisterRequest() {
        return UserRegisterRequest.builder().email(
                "test@test.com").username("John").userLastname("Doe")
                .password("Password").build();
    }

    public static UserRegisterRequest createUserRegisterRequest(String email, String username, String userLastname,
            String password) {
        return UserRegisterRequest.builder().email(email).username(username).userLastname(userLastname)
                .password(password).build();
    }

    public static UserRegisterRequest createUserRegisterRequest(String email) {
        return UserRegisterRequest.builder().email(email).username("John").userLastname("Doe")
                .password("Password").build();
    }

}
