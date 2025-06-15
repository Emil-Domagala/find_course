package emil.find_course.course.courseTeacher;

import java.util.UUID;

import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import emil.find_course.common.exception.ForbiddenException;
import emil.find_course.common.pagination.PaginationRequest;
import emil.find_course.common.pagination.PagingResult;
import emil.find_course.common.security.jwt.UserDetailsImpl;
import emil.find_course.course.CourseService;
import emil.find_course.course.dto.CourseDto;
import emil.find_course.course.dto.prot.CourseDetailsProtectedDto;
import emil.find_course.course.dto.request.CourseRequest;
import emil.find_course.course.enums.CourseCategory;
import emil.find_course.course.mapper.CourseMapper;
import emil.find_course.user.entity.User;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/teacher")
@RequiredArgsConstructor
public class CourseTeacherController {

    private final CourseService courseService;
    private final CourseTeacherService courseTeacherService;
    private final CourseMapper courseMapper;

    // Create empty course
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    @PostMapping("/courses")
    public ResponseEntity<CourseDto> postCourse(@AuthenticationPrincipal UserDetailsImpl userDetails) {

        final User user = userDetails.getUser();

        final CourseDto course = courseMapper
                .toDto(courseTeacherService.createCourse(user));

        return ResponseEntity.ok(course);

    }

    @PreAuthorize("hasRole('ROLE_TEACHER')")
    @GetMapping("/courses")
    public ResponseEntity<PagingResult<CourseDto>> getTeacherCourses(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sortField,
            @RequestParam(required = false) Sort.Direction direction,
            @RequestParam(required = false) CourseCategory category,
            @RequestParam(required = false) String keyword) {

        if (sortField == null || !CourseDto.ALLOWED_SORT_FIELDS.contains(sortField)) {
            sortField = "createdAt";
        }

        final User user = userDetails.getUser();
        final PaginationRequest request = new PaginationRequest(page, size, sortField, direction);
        final PagingResult<CourseDto> courses = courseTeacherService.searchTeacherCourses(keyword, category,
                request, user);
        return ResponseEntity.ok(courses);
    }

    @PreAuthorize("hasRole('ROLE_TEACHER')")
    @GetMapping("/courses/{courseId}")
    public ResponseEntity<CourseDetailsProtectedDto> getTeacherCourse(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable UUID courseId) {

        final CourseDetailsProtectedDto course = courseMapper.toProtectedDto(courseService.getById(courseId));
        if (course.getTeacher().getId() != userDetails.getUser().getId()) {
            throw new ForbiddenException("You are not the teacher of this course");
        }
        return ResponseEntity.ok(course);
    }

    // Update course
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    @PatchMapping("/courses/{courseId}")
    public ResponseEntity<Void> updateCourse(
            @AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable UUID courseId,
            @RequestPart("courseData") @Validated CourseRequest courseRequest,
            @RequestPart(required = false) MultipartFile image) {
        if (!courseRequest.getId().equals(courseId)) {
            throw new IllegalArgumentException("Mistmach beetwen request course id and path variable id");
        }

        final User user = userDetails.getUser();
        courseTeacherService.updateCourse(courseId, courseRequest, image, user);

        return ResponseEntity.noContent().build();
    }

    // Delete course
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    @DeleteMapping("/courses/{courseId}")
    public ResponseEntity<?> deleteCourse(@AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable UUID courseId) {
        final User user = userDetails.getUser();
        final UUID deletedCourseId = courseTeacherService.deleteCourse(courseId, user.getId());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(deletedCourseId);
    }

}
