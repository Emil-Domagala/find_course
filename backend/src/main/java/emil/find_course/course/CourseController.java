package emil.find_course.course;

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
import emil.find_course.course.dto.CourseDto;
import emil.find_course.course.dto.CourseDtoWithFirstChapter;
import emil.find_course.course.dto.prot.CourseDetailsProtectedDto;
import emil.find_course.course.dto.pub.CourseDetailsPublicDto;
import emil.find_course.course.dto.request.CourseRequest;
import emil.find_course.course.enums.CourseCategory;
import emil.find_course.course.mapper.CourseMapping;
import emil.find_course.user.entity.User;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;
    private final CourseMapping courseMapper;

    // **************************
    // ----------Public----------
    // **************************

    // Find published courses
    @GetMapping("public/courses")
    public ResponseEntity<PagingResult<CourseDto>> getCourses(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sortField,
            @RequestParam(required = false) Sort.Direction direction,
            @RequestParam(required = false) CourseCategory category,
            @RequestParam(required = false) String keyword) {

        if (sortField == null) {
            sortField = "createdAt";
        }

        final PaginationRequest request = new PaginationRequest(page, size, sortField, direction);
        final PagingResult<CourseDto> courses = courseService.searchCourses(keyword, category, request);
        return ResponseEntity.ok(courses);
    }

    // Show one published course
    @GetMapping("public/courses/{courseId}")
    public ResponseEntity<CourseDetailsPublicDto> getPublishedCourse(@PathVariable UUID courseId) {
        final CourseDetailsPublicDto course = courseMapper.toPublicDto(courseService.getPublishedCourse(courseId));
        return ResponseEntity.ok(course);

    }

    // **************************
    // ---------Teacher----------
    // **************************

    // Create empty course
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    @PostMapping("/teacher/courses")
    public ResponseEntity<CourseDto> postCourse(@AuthenticationPrincipal UserDetailsImpl userDetails) {

        final User user = userDetails.getUser();

        final CourseDto course = courseMapper
                .toDto(courseService.createCourse(user));

        return ResponseEntity.ok(course);

    }

    @PreAuthorize("hasRole('ROLE_TEACHER')")
    @GetMapping("/teacher/courses")
    public ResponseEntity<PagingResult<CourseDto>> getTeacherCourses(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sortField,
            @RequestParam(required = false) Sort.Direction direction,
            @RequestParam(required = false) CourseCategory category,
            @RequestParam(required = false) String keyword) {

        if (sortField == null) {
            sortField = "createdAt";
        }

        final User user = userDetails.getUser();
        final PaginationRequest request = new PaginationRequest(page, size, sortField, direction);
        final PagingResult<CourseDto> courses = courseService.searchTeacherCourses(keyword, category,
                request, user);
        return ResponseEntity.ok(courses);
    }

    @PreAuthorize("hasRole('ROLE_TEACHER')")
    @GetMapping("/teacher/courses/{courseId}")
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
    @PatchMapping("/teacher/courses/{courseId}")
    public ResponseEntity<Void> updateCourse(
            @AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable UUID courseId,
            @RequestPart("courseData") @Validated CourseRequest courseRequest,
            @RequestPart(required = false) MultipartFile image) {

        final User user = userDetails.getUser();
        courseService.updateCourse(courseId, courseRequest, image, user);

        return ResponseEntity.noContent().build();
    }

    // Delete course
    @DeleteMapping("/teacher/courses/{courseId}")
    public ResponseEntity<?> deleteCourse(@AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable UUID courseId) {
        final User user = userDetails.getUser();
        final UUID deletedCourseId = courseService.deleteCourse(courseId, user.getId());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(deletedCourseId);
    }

    // **************************
    // ---------Student----------
    // **************************

    // Show enrolled courses
    @GetMapping("/user/courses")
    public ResponseEntity<PagingResult<CourseDtoWithFirstChapter>> getUserEnrolledCourses(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sortField,
            @RequestParam(required = false) Sort.Direction direction) {

        if (sortField == null) {
            sortField = "createdAt";
        }

        final User user = userDetails.getUser();

        final PaginationRequest request = new PaginationRequest(page, size, sortField, direction);

        final PagingResult<CourseDtoWithFirstChapter> courses = courseService.getUserEnrolledCourses(user,
                request);
        return ResponseEntity.ok(courses);
    }

}
