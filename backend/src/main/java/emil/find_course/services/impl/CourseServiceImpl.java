package emil.find_course.services.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import emil.find_course.domains.entities.course.Course;
import emil.find_course.domains.entities.user.User;
import emil.find_course.domains.enums.CourseStatus;
import emil.find_course.domains.enums.Role;
import emil.find_course.domains.requestDto.RequestCourseBody;
import emil.find_course.repositories.CourseRepository;
import emil.find_course.services.CourseService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;

    @Override
    public List<Course> getCourses() {

        return courseRepository.findAllByStatus(CourseStatus.PUBLISHED);
    }

    @Override
    public Course getPublishedCourse(UUID id) {
        return courseRepository.findByIdAndStatus(id, CourseStatus.PUBLISHED);

    }

    @Override
    public Course createCourse(RequestCourseBody requestCourseBody, User teacher) {
        if (!teacher.getRoles().contains(Role.TEACHER)) {
            throw new IllegalStateException("Only teachers can create courses.");
        }
        Course course = Course.builder().teacher(teacher).title(requestCourseBody.getTitle())
                .description(requestCourseBody.getDescription()).category(requestCourseBody.getCategory())
                .imageUrl(requestCourseBody.getImageUrl()).price(requestCourseBody.getPrice())
                .level(requestCourseBody.getLevel()).status(requestCourseBody.getStatus()).build();

        return courseRepository.save(course);
    }

    @Override
    public Course updateCourse(RequestCourseBody requestCourseBody, User teacher) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateCourse'");
    }

    @Transactional
    @Override
    public UUID deleteCourse(UUID id, UUID teacherId) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));
        if (!course.getTeacher().getId().equals(teacherId)) {
            throw new IllegalArgumentException("You are not the teacher of this course");
        }
        courseRepository.delete(course);

        return id;
    }

}
