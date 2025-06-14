package emil.find_course.IntegrationTests.course;

import java.util.List;
import java.util.UUID;

import emil.find_course.course.entity.Course;
import emil.find_course.course.enums.CourseCategory;
import emil.find_course.course.enums.CourseStatus;
import emil.find_course.course.enums.Level;
import emil.find_course.course.section.entity.Section;
import emil.find_course.user.entity.User;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class CourseFactory {

    public static Course createCourse(User teacher) {
        String suffix = UUID.randomUUID().toString().substring(0, 15).toString();
        return Course.builder()
                .teacher(teacher)
                .title(suffix)
                .description(suffix)
                .category(CourseCategory.PROGRAMMING)
                .imageUrl("https://placehold.co/600x400")
                .price(0)
                .level(Level.BEGINNER).status(CourseStatus.DRAFT)
                .build();
    }

    public static Course createCourse(User teacher, List<Section> sections) {
        String suffix = UUID.randomUUID().toString().substring(0, 15).toString();
        return Course.builder()
                .teacher(teacher)
                .title(suffix)
                .description(suffix)
                .category(CourseCategory.PROGRAMMING)
                .imageUrl("https://placehold.co/600x400")
                .price(0)
                .level(Level.BEGINNER).status(CourseStatus.DRAFT)
                .sections(sections)
                .build();
    }

    // Published
    public static Course createPublishedCourse(User teacher) {
        String suffix = UUID.randomUUID().toString().substring(0, 15).toString();
        return Course.builder()
                .teacher(teacher)
                .title(suffix)
                .description(suffix)
                .category(CourseCategory.PROGRAMMING)
                .imageUrl("https://placehold.co/600x400")
                .price(0)
                .level(Level.BEGINNER).status(CourseStatus.PUBLISHED)
                .build();
    }

    public static Course createPublishedCourse(User teacher, List<Section> sections) {
        String suffix = UUID.randomUUID().toString().substring(0, 15).toString();
        return Course.builder()
                .teacher(teacher)
                .title(suffix)
                .description(suffix)
                .category(CourseCategory.PROGRAMMING)
                .imageUrl("https://placehold.co/600x400")
                .price(0)
                .level(Level.BEGINNER).status(CourseStatus.PUBLISHED)
                .sections(sections)
                .build();
    }

}
