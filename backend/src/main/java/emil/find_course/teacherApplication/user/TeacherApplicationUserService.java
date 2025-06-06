package emil.find_course.teacherApplication.user;

import java.util.Optional;

import emil.find_course.teacherApplication.entity.TeacherApplication;
import emil.find_course.user.entity.User;

public interface TeacherApplicationUserService {

    public Optional<TeacherApplication> getUserTeacherApplication(User user);

    public TeacherApplication createTeacherApplication(User user);

}
