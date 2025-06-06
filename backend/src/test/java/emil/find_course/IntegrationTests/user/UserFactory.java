package emil.find_course.IntegrationTests.user;

import emil.find_course.user.entity.User;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class UserFactory {

    public static User createVerifiedUser() {
        return User.builder().email("test@test.com").username("John").userLastname("Doe")
                .password("Password").isEmailVerified(true).build();
    }

    public static User createVerifiedUser(String email, String name) {
        return User.builder().email(email).username(name).userLastname("Doe")
                .password("Password").isEmailVerified(true).build();
    }

    public static User createNotVerifiedUser() {
        return User.builder().email("test@test.com").username("John").userLastname("Doe")
                .password("Password").build();
    }

}
