package emil.find_course.services;

import java.util.UUID;

import emil.find_course.domains.dto.CourseDto;
import emil.find_course.domains.entities.course.Course;
import emil.find_course.domains.entities.user.User;
import emil.find_course.domains.enums.CourseCategory;
import emil.find_course.domains.pagination.PaginationRequest;
import emil.find_course.domains.pagination.PagingResult;

public interface CourseService {

    // Public
    public Course getPublishedCourse(UUID id);

    public PagingResult<CourseDto> searchCourses(String keyword, CourseCategory category, PaginationRequest request);

    // Teacher

    public Course createCourse(User teacher);

    public PagingResult<CourseDto> searchTeacherCourses(String keyword, CourseCategory category,
            PaginationRequest request,
            User teacher);

    // Course updateCourse(RequestCourseBody requestCourseBody, User teacher);

    public UUID deleteCourse(UUID id, UUID teacherId);

    // Student
    public PagingResult<CourseDto> getUserEnrolledCourses(User student, PaginationRequest request);
}
