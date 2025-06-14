package emil.find_course.IntegrationTests.course;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import emil.find_course.IntegrationTests.teacherApplication.PrepareTeacherUtil;
import emil.find_course.course.chapter.repository.ChapterRepository;
import emil.find_course.course.entity.Course;
import emil.find_course.course.repository.CourseRepository;
import emil.find_course.course.section.entity.Section;
import emil.find_course.course.section.repository.SectionRepository;
import emil.find_course.user.entity.User;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PrepareCourseUtil {
    private final CourseRepository courseRepository;
    private final SectionRepository sectionRepository;
    private final ChapterRepository chapterRepository;
    private final PrepareTeacherUtil prepareTeacherUtil;

    public Course prepareDraftCourse() {
        User teacher = prepareTeacherUtil.prepareUniqueTeacher();
        var savedCourse = courseRepository.save(CourseFactory.createCourse(teacher));
        assertThat(courseRepository.count()).isEqualTo(1);
        return savedCourse;
    }

    public Course prepareDraftCourseWithSectionsAndChapters(int count) {
        User teacher = prepareTeacherUtil.prepareUniqueTeacher();
        Course course = CourseFactory.createCourse(teacher);
        List<Section> sections = SectionFactory.createSectionsWithChapters(course, count);
        course.setSections(sections);
        var savedCourse = courseRepository.save(course);
        assertThat(savedCourse);
        assertThat(sectionRepository.count()).isEqualTo(count);
        assertThat(chapterRepository.count()).isEqualTo(count * count);
        return savedCourse;
    }

    // Published

    public Course prepareCourse() {
        User teacher = prepareTeacherUtil.prepareUniqueTeacher();
        var savedCourse = courseRepository.save(CourseFactory.createPublishedCourse(teacher));
        assertThat(courseRepository.count()).isEqualTo(1);
        return savedCourse;
    }

    public List<Course> prepareCourses(int count) {
        List<Course> courses = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            User teacher = prepareTeacherUtil.prepareUniqueTeacher();
            var savedCourse = courseRepository.save(CourseFactory.createPublishedCourse(teacher));
            courses.add(savedCourse);
        }

        assertThat(courseRepository.count()).isEqualTo(count);
        return courses;
    }

    public Course prepareCourseWithSectionsAndChapters(int count) {
        User teacher = prepareTeacherUtil.prepareUniqueTeacher();
        Course course = CourseFactory.createPublishedCourse(teacher);
        List<Section> sections = SectionFactory.createSectionsWithChapters(course, count);
        course.setSections(sections);
        var savedCourse = courseRepository.save(course);
        assertThat(savedCourse);
        assertThat(sectionRepository.count()).isEqualTo(count);
        assertThat(chapterRepository.count()).isEqualTo(count * count);
        return savedCourse;
    }

    public Course prepareCourseWithSections(int count) {
        User teacher = prepareTeacherUtil.prepareUniqueTeacher();
        Course course = CourseFactory.createPublishedCourse(teacher);
        List<Section> sections = SectionFactory.createSections(course, count);
        course.setSections(sections);
        var savedCourse = courseRepository.save(course);
        assertThat(savedCourse);
        assertThat(sectionRepository.count()).isEqualTo(count);
        return savedCourse;
    }
}
