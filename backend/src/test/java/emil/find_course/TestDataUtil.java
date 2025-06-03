package emil.find_course;

import java.util.Set;

import emil.find_course.course.entity.Course;
import emil.find_course.course.enums.CourseCategory;
import emil.find_course.course.enums.CourseStatus;
import emil.find_course.course.enums.Level;
import emil.find_course.user.entity.User;
import emil.find_course.user.enums.Role;

public final class TestDataUtil {
    private TestDataUtil() {
    }

    public static User createVerifiedUser() {
        return User.builder().email("test@test.com").username("John").userLastname("Doe")
                .password("Password").isEmailVerified(true).build();
    }

    public static User createVerifiedUser(String email, String name) {
        return User.builder().email(email).username(name).userLastname("Doe")
                .password("Password").isEmailVerified(true).build();
    }

    public static User createTeacher(String email, String name) {
        return User.builder().email(email).username(name).userLastname("Doe").roles(Set.of(Role.TEACHER,
                Role.USER))
                .password("Password").isEmailVerified(true).build();
    }

    public static User createTeacher() {
        return User.builder().email("test@test.com").username("John").userLastname("Doe").roles(Set.of(Role.TEACHER,
                Role.USER))
                .password("Password").isEmailVerified(true).build();
    }

    public static User createAdmin() {
        return User.builder().email("test@test.com").username("John").userLastname("Doe").roles(Set.of(Role.TEACHER,
                Role.USER, Role.ADMIN))
                .password("Password").isEmailVerified(true).build();
    }

    public static User createNotVerifiedUser() {
        return User.builder().email("test@test.com").username("John").userLastname("Doe")
                .password("Password").build();
    }

    public static Course createPublishedCourse(User teacher) {
        return Course.builder().teacher(teacher).title("New Course").description("New Course Description")
                .category(CourseCategory.PROGRAMMING).price(9999).level(Level.INTERMEDIATE)
                .status(CourseStatus.PUBLISHED).build();
    }

    public static Course createDraftCourse(User teacher) {
        return Course.builder().teacher(teacher).title("New Course").description("New Course Description")
                .category(CourseCategory.PROGRAMMING).price(9999).level(Level.INTERMEDIATE).status(CourseStatus.DRAFT)
                .build();
    }

}
