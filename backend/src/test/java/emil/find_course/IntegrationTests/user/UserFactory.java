package emil.find_course.IntegrationTests.user;

import emil.find_course.user.entity.User;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class UserFactory {

        public final String BASE_PASSWORD = "Password";
        public final String BASE_EMAIL = "test@test.com";
        public final String BASE_NAME = "John";

        public static User createUniqueVerifiedUser() {
                String uniqueSuffix = java.util.UUID.randomUUID().toString().substring(0, 15);
                return User.builder()
                                .email(uniqueSuffix + "@test.com")
                                .username(uniqueSuffix)
                                .userLastname(uniqueSuffix)
                                .password(BASE_PASSWORD)
                                .isEmailVerified(true).build();
        }

        public static User createVerifiedUser() {
                return User.builder()
                                .email(BASE_EMAIL)
                                .username(BASE_NAME)
                                .userLastname("Doe")
                                .password(BASE_PASSWORD)
                                .isEmailVerified(true).build();
        }

        public static User createVerifiedUser(String email, String name) {
                return User.builder()
                                .email(email)
                                .username(name)
                                .userLastname("Doe")
                                .password(BASE_PASSWORD)
                                .isEmailVerified(true).build();
        }

        public static User createNotVerifiedUser() {
                return User.builder()
                                .email(BASE_EMAIL)
                                .username(BASE_NAME)
                                .userLastname("Doe")
                                .password(BASE_PASSWORD)
                                .build();
        }

        public static User createNotVerifiedUser(String email, String name) {
                return User.builder()
                                .email(email)
                                .username(name)
                                .userLastname("Doe")
                                .password(BASE_PASSWORD)
                                .build();
        }

}
