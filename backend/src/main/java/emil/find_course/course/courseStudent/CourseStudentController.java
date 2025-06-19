package emil.find_course.course.courseStudent;

import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import emil.find_course.common.pagination.PaginationRequest;
import emil.find_course.common.pagination.PagingResult;
import emil.find_course.common.security.jwt.UserDetailsImpl;
import emil.find_course.course.dto.CourseDtoWithFirstChapter;
import emil.find_course.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;


@Tag(name = "Course Student Controller", description = "Endpoints for Course Student")
@RestController
@RequestMapping("/api/v1/student") // TODO: Fix route on frontend
@RequiredArgsConstructor
public class CourseStudentController {
    private final CourseStudentService courseStudentService;

    // Show enrolled courses with first chapter of first section
    @Operation(summary = "Get User Enrolled Courses", description = "Get Curses that User is enrolled in")
    @GetMapping("/courses")
    public ResponseEntity<PagingResult<CourseDtoWithFirstChapter>> getUserEnrolledCourses(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sortField,
            @RequestParam(required = false) Sort.Direction direction) {

        if (sortField == null || !CourseDtoWithFirstChapter.ALLOWED_SORT_FIELDS.contains(sortField)) {
            sortField = "createdAt";
        }

        final User user = userDetails.getUser();

        final PaginationRequest request = new PaginationRequest(page, size, sortField, direction);

        final PagingResult<CourseDtoWithFirstChapter> courses = courseStudentService.getUserEnrolledCourses(user,
                request);
        return ResponseEntity.ok(courses);
    }
}
