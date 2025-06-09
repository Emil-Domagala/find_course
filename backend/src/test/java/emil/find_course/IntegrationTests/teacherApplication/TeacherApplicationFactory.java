package emil.find_course.IntegrationTests.teacherApplication;

import emil.find_course.teacherApplication.entity.TeacherApplication;
import emil.find_course.teacherApplication.enums.TeacherApplicationStatus;
import emil.find_course.user.entity.User;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class TeacherApplicationFactory {

    public static TeacherApplication createTeacherApplication(User user) {
        return TeacherApplication.builder().user(user).build();
    }

    public static TeacherApplication createTeacherApplication(User user, Boolean seenByAdmin) {
        return TeacherApplication.builder().user(user).seenByAdmin(seenByAdmin).build();
    }

    public static TeacherApplication createTeacherApplication(User user, Boolean seenByAdmin,
            TeacherApplicationStatus status) {
        if (!status.equals(TeacherApplicationStatus.PENDING)) {
            seenByAdmin = true;
        }
        return TeacherApplication.builder().user(user).seenByAdmin(seenByAdmin).status(status).build();
    }
}
