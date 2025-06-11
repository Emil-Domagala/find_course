package emil.find_course.IntegrationTests.course.courseStudent;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.springframework.stereotype.Component;

import emil.find_course.IntegrationTests.course.CourseFactory;
import emil.find_course.IntegrationTests.course.SectionFactory;
import emil.find_course.IntegrationTests.teacherApplication.PrepareTeacherUtil;
import emil.find_course.course.entity.Course;
import emil.find_course.course.repository.CourseRepository;
import emil.find_course.course.section.entity.Section;
import emil.find_course.user.entity.User;
import emil.find_course.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PrepareCourseWithStudentUtil {

    private final CourseRepository courseRepository;
    private final PrepareTeacherUtil prepareTeacherUtil;
    private final UserRepository userRepository;

    public Course prepareCourseWithChapters(User student, int count) {
        User teacher = prepareTeacherUtil.prepareUniqueTeacher();
        var course = CourseFactory.createPublishedCourse(teacher);
        course.getStudents().add(student);
        student.getEnrollmentCourses().add(course);

        List<Section> sections = SectionFactory.createSectionsWithChapters(course, count);
        course.setSections(sections);
        Course savedCourse = courseRepository.save(course);
        assertThat(savedCourse);
        userRepository.save(student);
        return savedCourse;
    }

    public Course prepareCourse(User student, int count) {
        User teacher = prepareTeacherUtil.prepareUniqueTeacher();
        var course = CourseFactory.createPublishedCourse(teacher);
        course.getStudents().add(student);
        student.getEnrollmentCourses().add(course);
        Course savedCourse = courseRepository.save(course);
        userRepository.save(student);
        return savedCourse;
    }
}
