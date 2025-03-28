package emil.find_course.controllers;

// import java.security.Principal;
import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.PutMapping;
// import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import emil.find_course.domains.dto.CourseDto;
import emil.find_course.domains.dto.detailsPub.CourseDetailsPublicDto;
import emil.find_course.mapping.CourseMapping;
import emil.find_course.services.CourseService;
// import emil.find_course.services.UserService;
// import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CourseController {

    // Ok So i need to have API Endpoinds for:

    // User
    // 1) Showing all courses in order to show them for sale.(Title, image, author)
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
    // private final UserService userService;
    private final CourseMapping courseMapper;

    // Show all courses
    @GetMapping("/courses")
    public ResponseEntity<List<CourseDto>> getCourses() {
        List<CourseDto> courses = courseService.getCourses().stream().map(courseMapper::toDto).toList();
        return ResponseEntity.ok(courses);
    }

    // Show one course
    @GetMapping("/courses/{id}")
    public ResponseEntity<CourseDetailsPublicDto> getPublishedCourse(@PathVariable UUID id) {
        CourseDetailsPublicDto course = courseMapper.toPublicDto(courseService.getPublishedCourse(id));
        return ResponseEntity.ok(course);

    }

    // @GetMapping("/{userId}/courses")
    // public ResponseEntity<List<CourseDto>> getUserCourses(@PathVariable UUID
    // userId) {
    // }

    // @PostMapping("/courses")
    //
    // public ResponseEntity<CourseDto> postCourse(Principal principal, @RequestBody
    // RequestCourseBody requestCourseBody) {

    // }

    // @PutMapping("/courses/{id}")
    //
    // public ResponseEntity<CourseDto> putCourse(Principal principal, @PathVariable
    // UUID
    // id,
    // @RequestBody RequestCourseBody requestCourseBody) {

    // }

    // @DeleteMapping("/courses/{id}")
    //
    // public ResponseEntity<UUID> deleteCourse(Principal principal, @PathVariable
    // UUID id) {

    // }

}
