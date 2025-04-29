package emil.find_course.controllers;

import java.security.Principal;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

import emil.find_course.domains.dto.course.CourseDto;
import emil.find_course.domains.dto.course.CourseDtoWithFirstChapter;
import emil.find_course.domains.dto.detailsProt.CourseDetailsProtectedDto;
import emil.find_course.domains.dto.detailsPub.CourseDetailsPublicDto;
import emil.find_course.domains.entities.user.User;
import emil.find_course.domains.enums.CourseCategory;
import emil.find_course.domains.pagination.PaginationRequest;
import emil.find_course.domains.pagination.PagingResult;
import emil.find_course.domains.requestDto.course.CourseRequest;
import emil.find_course.exceptions.UnauthorizedException;
import emil.find_course.mapping.CourseMapping;
import emil.find_course.services.CourseService;
import emil.find_course.services.UserService;
// import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CourseController {

    // Ok So i need to have API Endpoinds for:

    // User
    // 1) Showing all published courses in order to show them for sale.(Title,
    // image, author)
    // 2) Showing course details for sale (chapters, sections, NO CONTENT)
    // 3) Showing enrolled courses
    // 4) showing enrolled course detail (full access)

    // Teacher
    // 5) Showing created courses (Published and Drafts)
    // 6) Shwoing details of created course
    // 7) Create course
    // 8) Delete course (In real life shouldnt be possible if anyone enrolled)
    // 9) Change course

    private final CourseService courseService;
    private final UserService userService;
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
        if (size > 100) {
            size = 100;
        }
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
    public ResponseEntity<CourseDto> postCourse(Principal principal) {

        final User user = userService.findByEmail(principal.getName());

        final CourseDto course = courseMapper
                .toDto(courseService.createCourse(user));

        return ResponseEntity.ok(course);

    }

    @PreAuthorize("hasRole('ROLE_TEACHER')")
    @GetMapping("/teacher/courses")
    public ResponseEntity<PagingResult<CourseDto>> getTeacherCourses(Principal principal,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sortField,
            @RequestParam(required = false) Sort.Direction direction,
            @RequestParam(required = false) CourseCategory category,
            @RequestParam(required = false) String keyword) {
        if (size > 100) {
            size = 100;
        }
        if (sortField == null) {
            sortField = "createdAt";
        }

        final User user = userService.findByEmail(principal.getName());
        final PaginationRequest request = new PaginationRequest(page, size, sortField, direction);
        final PagingResult<CourseDto> courses = courseService.searchTeacherCourses(keyword, category,
                request, user);
        return ResponseEntity.ok(courses);
    }

    @PreAuthorize("hasRole('ROLE_TEACHER')")
    @GetMapping("/teacher/courses/{courseId}")
    public ResponseEntity<CourseDetailsProtectedDto> getTeacherCourse(Principal principal,
            @PathVariable UUID courseId) {
        System.out.println(courseId);

        final CourseDetailsProtectedDto course = courseMapper.toProtectedDto(courseService.getById(courseId));
        if (course.getTeacher().getId() != userService.findByEmail(principal.getName()).getId()) {
            throw new UnauthorizedException("You are not the teacher of this course");
        }
        return ResponseEntity.ok(course);
    }

    // Update course
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    @PatchMapping("/teacher/courses/{courseId}")
    public ResponseEntity<Void> updateCourse(
            Principal principal, @PathVariable UUID courseId,
            @RequestPart("courseData") @Validated CourseRequest courseRequest,
            @RequestPart(required = false) MultipartFile image,
            @RequestPart(required = false) Map<String, MultipartFile> videos) {

        final User user = userService.findByEmail(principal.getName());
        courseService.updateCourse(courseId, courseRequest, image, user, videos);

        return ResponseEntity.noContent().build();
    }

    // Delete course
    @DeleteMapping("/teacher/courses/{courseId}")
    public ResponseEntity<?> deleteCourse(Principal principal, @PathVariable UUID courseId) {
        final User user = userService.findByEmail(principal.getName());
        final UUID deletedCourseId = courseService.deleteCourse(courseId, user.getId());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(deletedCourseId);
    }

    // **************************
    // ---------Student----------
    // **************************

    // Show enrolled courses
    @GetMapping("/user/courses")
    public ResponseEntity<PagingResult<CourseDtoWithFirstChapter>> getUserEnrolledCourses(Principal principal,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sortField,
            @RequestParam(required = false) Sort.Direction direction) {
        if (size > 100) {
            size = 100;
        }
        if (sortField == null) {
            sortField = "createdAt";
        }

        final User user = userService.findByEmail(principal.getName());

        final PaginationRequest request = new PaginationRequest(page, size, sortField, direction);

        final PagingResult<CourseDtoWithFirstChapter> courses = courseService.getUserEnrolledCourses(user,
                request);
        return ResponseEntity.ok(courses);
    }

}
