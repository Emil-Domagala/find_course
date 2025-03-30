package emil.find_course.controllers;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import emil.find_course.domains.dto.CourseDto;
import emil.find_course.domains.dto.detailsPub.CourseDetailsPublicDto;
import emil.find_course.domains.entities.user.User;
import emil.find_course.domains.pagination.PaginationRequest;
import emil.find_course.domains.pagination.PagingResult;
import emil.find_course.domains.requestDto.RequestCourseBody;
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

    // Show all published courses
    @GetMapping("public/courses")
    public ResponseEntity<PagingResult<CourseDto>> getCourses(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sortField,
            @RequestParam(required = false) Sort.Direction direction) {
        final PaginationRequest request = new PaginationRequest(page, size, sortField, direction);
        final PagingResult<CourseDto> courses = courseService.getPublishedCourses(request);
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

    // Create course
    @PostMapping("/courses")
    public ResponseEntity<?> postCourse(Principal principal,
            @Validated @RequestBody RequestCourseBody requestCourseBody) {

        final User user = userService.findByEmail(principal.getName());

        final CourseDto course = courseMapper
                .toDto(courseService.createCourse(requestCourseBody, user));

        return ResponseEntity.ok(course);

    }

    // Update course
    // @PutMapping("/courses/{courseId}")
    // public ResponseEntity<CourseDto> putCourse(Principal principal, @PathVariable
    // UUID courseId,
    // @Validated @RequestBody RequestCourseBody requestCourseBody) {

    // }

    // Delete course
    @DeleteMapping("/courses/{courseId}")
    public ResponseEntity<UUID> deleteCourse(Principal principal, @PathVariable UUID courseId) {
        final User user = userService.findByEmail(principal.getName());

        final UUID deletedCourseId = courseService.deleteCourse(courseId, user.getId());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(deletedCourseId);
    }

    // **************************
    // ---------Student----------
    // **************************

    // Show enrolled courses
    @GetMapping("/{userId}/courses")
    public ResponseEntity<PagingResult<CourseDto>> getUserEnrolledCourses(Principal principal,
            @PathVariable UUID userId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sortField,
            @RequestParam(required = false) Sort.Direction direction) {
        final User user = userService.findByEmail(principal.getName());

        if (userId != user.getId()) {
            throw new IllegalArgumentException("Tried to access another user's courses");
        }
        final PaginationRequest request = new PaginationRequest(page, size, sortField, direction);

        final PagingResult<CourseDto> courses = courseService.getUserEnrolledCourses(user,
                request);
        return ResponseEntity.ok(courses);
    }

}
