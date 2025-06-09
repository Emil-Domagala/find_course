package emil.find_course.teacherApplication.user;

import java.util.Optional;

import org.springframework.stereotype.Service;

import emil.find_course.teacherApplication.entity.TeacherApplication;
import emil.find_course.teacherApplication.repository.TeacherApplicationRepository;
import emil.find_course.user.entity.User;
import emil.find_course.user.enums.Role;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TeacherApplicationUserServiceImpl implements TeacherApplicationUserService {

    private final TeacherApplicationRepository teacherApplicationRepository;

    @Override
    public Optional<TeacherApplication> getUserTeacherApplication(User user) {
        return teacherApplicationRepository.findByUser(user);
    }

    @Override
    public TeacherApplication createTeacherApplication(User user) {
        Optional<TeacherApplication> teacherApplicationOpt = teacherApplicationRepository.findByUser(user);
        if (user.getRoles().contains(Role.TEACHER)) {
            throw new IllegalArgumentException("You Are already a Teacher");
        }
        if (teacherApplicationOpt.isPresent()) {
            throw new IllegalArgumentException("You already send teacher application request");
        }
        TeacherApplication newTeacherApplication = new TeacherApplication();
        newTeacherApplication.setUser(user);
        return teacherApplicationRepository.save(newTeacherApplication);
    }

}
