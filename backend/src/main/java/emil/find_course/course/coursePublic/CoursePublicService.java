package emil.find_course.course.coursePublic;

import java.util.UUID;

import emil.find_course.common.pagination.PaginationRequest;
import emil.find_course.common.pagination.PagingResult;
import emil.find_course.course.dto.CourseDto;
import emil.find_course.course.entity.Course;
import emil.find_course.course.enums.CourseCategory;

public interface CoursePublicService {
    public Course getPublishedCourse(UUID id);

    public PagingResult<CourseDto> searchCourses(String keyword, CourseCategory category, PaginationRequest request);

}
