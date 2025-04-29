package emil.find_course.services;

import java.util.Map;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import emil.find_course.domains.dto.course.CourseDto;
import emil.find_course.domains.dto.course.CourseDtoWithFirstChapter;
import emil.find_course.domains.entities.course.Course;
import emil.find_course.domains.entities.user.User;
import emil.find_course.domains.enums.CourseCategory;
import emil.find_course.domains.pagination.PaginationRequest;
import emil.find_course.domains.pagination.PagingResult;
import emil.find_course.domains.requestDto.course.CourseRequest;

public interface CourseService {

    // Public
    public Course getPublishedCourse(UUID id);

    public PagingResult<CourseDto> searchCourses(String keyword, CourseCategory category, PaginationRequest request);

    // Teacher

    public Course createCourse(User teacher);

    public PagingResult<CourseDto> searchTeacherCourses(String keyword, CourseCategory category,
            PaginationRequest request,
            User teacher);

    public UUID deleteCourse(UUID id, UUID teacherId);

    public Course getById(UUID id);

    public void updateCourse(UUID courseId, CourseRequest courseRequest, MultipartFile image,User user, Map<String,MultipartFile> videos);

    // Student
    public PagingResult<CourseDtoWithFirstChapter> getUserEnrolledCourses(User student, PaginationRequest request);
}
