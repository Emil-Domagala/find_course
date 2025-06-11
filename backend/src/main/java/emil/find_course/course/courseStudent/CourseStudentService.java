package emil.find_course.course.courseStudent;

import emil.find_course.common.pagination.PaginationRequest;
import emil.find_course.common.pagination.PagingResult;
import emil.find_course.course.dto.CourseDtoWithFirstChapter;
import emil.find_course.user.entity.User;

public interface CourseStudentService {
     public PagingResult<CourseDtoWithFirstChapter> getUserEnrolledCourses(User student, PaginationRequest request);
}
