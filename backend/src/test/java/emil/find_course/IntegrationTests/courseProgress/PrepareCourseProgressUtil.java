package emil.find_course.IntegrationTests.courseProgress;

import org.springframework.stereotype.Component;

import emil.find_course.course.entity.Course;
import emil.find_course.courseProgress.entity.CourseProgress;
import emil.find_course.courseProgress.repository.CourseProgressRepository;
import emil.find_course.user.entity.User;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PrepareCourseProgressUtil {

    private final CourseProgressRepository courseProgressRepository;

    public CourseProgress createCourseProgress(Course course, User user) {

        var courseProgress = CourseProgress.builder().course(course).user(user).build();
        var sectionsProgress = SectionProgressFactory.createSectionProgress(course.getSections(), courseProgress);
        courseProgress.setSections(sectionsProgress);
        courseProgressRepository.save(courseProgress);

        return courseProgress;
    }

}
