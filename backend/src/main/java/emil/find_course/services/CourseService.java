package emil.find_course.services;

import java.util.UUID;

import emil.find_course.domains.dto.CourseDto;
import emil.find_course.domains.entities.course.Course;
import emil.find_course.domains.entities.user.User;
import emil.find_course.domains.pagination.PaginationRequest;
import emil.find_course.domains.pagination.PagingResult;
import emil.find_course.domains.requestDto.RequestCourseBody;

public interface CourseService {

    // Public
    Course getPublishedCourse(UUID id);

    PagingResult<CourseDto> getPublishedCourses(PaginationRequest request);

    // Teacher

    Course createCourse(RequestCourseBody requestCourseBody, User teacher);

    // Course updateCourse(RequestCourseBody requestCourseBody, User teacher);

    UUID deleteCourse(UUID id, UUID teacherId);

    // Student
    PagingResult<CourseDto> getUserEnrolledCourses(User student, PaginationRequest request);
}
