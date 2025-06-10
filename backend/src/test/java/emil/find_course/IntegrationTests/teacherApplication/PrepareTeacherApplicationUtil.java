package emil.find_course.IntegrationTests.teacherApplication;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.UUID;

import org.springframework.stereotype.Component;

import emil.find_course.IntegrationTests.user.PrepareUserUtil;
import emil.find_course.IntegrationTests.user.UserFactory;
import emil.find_course.teacherApplication.entity.TeacherApplication;
import emil.find_course.teacherApplication.enums.TeacherApplicationStatus;
import emil.find_course.teacherApplication.repository.TeacherApplicationRepository;
import emil.find_course.user.entity.User;
import emil.find_course.user.enums.Role;
import emil.find_course.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PrepareTeacherApplicationUtil {

    private final TeacherApplicationRepository repo;

    private final TeacherApplicationRepository teacherApplicationRepository;
    private final UserRepository userRepository;
    private final PrepareUserUtil prepareUserUtil;

    private void validateUser(User user) {
        if (user.getRoles().contains(Role.TEACHER)) {
            throw new RuntimeException("user cannot have role teacher");
        }
        if (!user.isEmailVerified()) {
            throw new RuntimeException("User must be verified");
        }
    }

    public TeacherApplication praparUniqueTeacherApplication() {
        String uniqueSuffix = UUID.randomUUID().toString().substring(0, 15);
        var user = prepareUserUtil.prepareVerifiedUser(uniqueSuffix + "@gmail.com", uniqueSuffix);
        var savedApp = repo.save(TeacherApplicationFactory.createTeacherApplication(user));
        assertThat(savedApp);

        return savedApp;
    }

    public TeacherApplication praparTeacherApplication(User user) {
        validateUser(user);
        var savedApp = repo.save(TeacherApplicationFactory.createTeacherApplication(user));
        assertThat(savedApp);

        return savedApp;
    }

    public TeacherApplication praparTeacherApplication(User user, Boolean seenByAdmin) {
        validateUser(user);
        var savedApp = repo.save(TeacherApplicationFactory.createTeacherApplication(user, seenByAdmin));
        assertThat(savedApp);

        return savedApp;
    }

    public TeacherApplication praparTeacherApplication(User user, Boolean seenByAdmin,
            TeacherApplicationStatus status) {
        validateUser(user);
        var savedApp = repo.save(TeacherApplicationFactory.createTeacherApplication(user, seenByAdmin, status));
        assertThat(savedApp);
        return savedApp;
    }

    public ArrayList<TeacherApplication> createAndPersistUniqueUserAndTeacherApplications(int count) {
        validateCount(count);
        ArrayList<User> users = new ArrayList<User>();
        ArrayList<TeacherApplication> teacherApplications = new ArrayList<TeacherApplication>();

        for (int i = 0; i < count; i++) {
            String uniqueSuffix = UUID.randomUUID().toString().substring(0, 15);
            User user = UserFactory.createVerifiedUser(uniqueSuffix + "@gmail.com", uniqueSuffix);
            users.add(user);
            teacherApplications.add(TeacherApplicationFactory.createTeacherApplication(user));
        }
        saveAndAssert(users, teacherApplications, count);

        return teacherApplications;
    }

    public ArrayList<TeacherApplication> createAndPersistUniqueUserAndTeacherApplications(int count,
            Boolean seenByAdmin) {
        validateCount(count);
        ArrayList<User> users = new ArrayList<User>();
        ArrayList<TeacherApplication> teacherApplications = new ArrayList<TeacherApplication>();

        for (int i = 0; i < count; i++) {
            String uniqueSuffix = UUID.randomUUID().toString().substring(0, 15);
            User user = UserFactory.createVerifiedUser(uniqueSuffix + "@gmail.com", uniqueSuffix);
            users.add(user);
            teacherApplications.add(TeacherApplicationFactory.createTeacherApplication(user, seenByAdmin));
        }
        saveAndAssert(users, teacherApplications, count);

        return teacherApplications;
    }

    public ArrayList<TeacherApplication> createAndPersistUniqueUserAndTeacherApplications(int count,
            Boolean seenByAdmin, TeacherApplicationStatus status) {
        validateCount(count);
        ArrayList<User> users = new ArrayList<User>();
        ArrayList<TeacherApplication> teacherApplications = new ArrayList<TeacherApplication>();

        for (int i = 0; i < count; i++) {
            String uniqueSuffix = UUID.randomUUID().toString().substring(0, 15);
            User user = UserFactory.createVerifiedUser(uniqueSuffix + "@gmail.com", uniqueSuffix);
            users.add(user);
            teacherApplications.add(TeacherApplicationFactory.createTeacherApplication(user, seenByAdmin, status));
        }

        saveAndAssert(users, teacherApplications, count);

        return teacherApplications;
    }

    private void saveAndAssert(ArrayList<User> users, ArrayList<TeacherApplication> teacherApplications, int count) {
        long initialUserCount = userRepository.count();
        long initialTeacherApplicationCount = teacherApplicationRepository.count();

        userRepository.saveAll(users);
        teacherApplicationRepository.saveAll(teacherApplications);

        assertThat(userRepository.count()).isEqualTo(initialUserCount + count);
        assertThat(teacherApplicationRepository.count()).isEqualTo(initialTeacherApplicationCount + count);
    }

    private void validateCount(int count) {
        if (count == 0) {
            return;
        }
        if (count > 200) {
            throw new RuntimeException("Too many applications may slow down tests");
        }
    }
}
