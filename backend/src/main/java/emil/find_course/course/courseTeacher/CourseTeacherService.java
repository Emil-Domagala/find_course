package emil.find_course.course.courseTeacher;

import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import emil.find_course.common.pagination.PaginationRequest;
import emil.find_course.common.pagination.PagingResult;
import emil.find_course.course.dto.CourseDto;
import emil.find_course.course.dto.request.CourseRequest;
import emil.find_course.course.entity.Course;
import emil.find_course.course.enums.CourseCategory;
import emil.find_course.user.entity.User;

public interface CourseTeacherService {
        public Course createCourse(User teacher);

    public PagingResult<CourseDto> searchTeacherCourses(String keyword, CourseCategory category,
            PaginationRequest request,
            User teacher);

    public UUID deleteCourse(UUID id, UUID teacherId);

    public void updateCourse(UUID courseId, CourseRequest courseRequest, MultipartFile image, User user);
}
