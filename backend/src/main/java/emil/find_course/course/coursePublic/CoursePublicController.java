package emil.find_course.course.coursePublic;

import java.util.UUID;

import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import emil.find_course.common.pagination.PaginationRequest;
import emil.find_course.common.pagination.PagingResult;
import emil.find_course.course.dto.CourseDto;
import emil.find_course.course.dto.pub.CourseDetailsPublicDto;
import emil.find_course.course.enums.CourseCategory;
import emil.find_course.course.mapper.CourseMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Course Public Controller", description = "Endpoints for Course Public")
@RestController
@RequestMapping("/api/v1/public")
@RequiredArgsConstructor
public class CoursePublicController {

    private final CoursePublicService coursePublicService;
    private final CourseMapper courseMapper;

    // Find published courses
    @Operation(summary = "Search Published Courses", description = "Search Published Courses")
    @GetMapping("/courses")
    public ResponseEntity<PagingResult<CourseDto>> getCourses(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sortField,
            @RequestParam(required = false) Sort.Direction direction,
            @RequestParam(required = false) CourseCategory category,
            @RequestParam(required = false) String keyword) {

        if (sortField == null || !CourseDto.ALLOWED_SORT_FIELDS.contains(sortField)) {
            sortField = "createdAt";
        }

        final PaginationRequest request = new PaginationRequest(page, size, sortField, direction);
        final PagingResult<CourseDto> courses = coursePublicService.searchCourses(keyword, category, request);
        return ResponseEntity.ok(courses);
    }

    // Show one published course
    @Operation(summary = "Get Published Course", description = "Get Published Course")
    @GetMapping("/courses/{courseId}")
    public ResponseEntity<CourseDetailsPublicDto> getPublishedCourse(@PathVariable UUID courseId) {
        final CourseDetailsPublicDto course = courseMapper
                .toPublicDto(coursePublicService.getPublishedCourse(courseId));
        return ResponseEntity.ok(course);

    }
}
