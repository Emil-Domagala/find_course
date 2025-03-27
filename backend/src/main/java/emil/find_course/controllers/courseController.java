package emil.find_course.controllers;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import emil.find_course.domains.dto.CourseDto;
import emil.find_course.mapping.CourseMapping;
import emil.find_course.services.CourseService;
import emil.find_course.services.UserService;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;
    private final UserService userService;
    private final CourseMapping courseMapper;

    @GetMapping("/courses")
    public ResponseEntity<List<CourseDto>> getCourses() {
        List<CourseDto> courses = courseService.getCourses().stream().map(courseMapper::toDto).toList();
        return ResponseEntity.ok(courses);
    }

    // @GetMapping("/courses/{id}")
    // public ResponseEntity<CourseDto> getCourse(@PathVariable UUID id) {

    // }

    // @PostMapping("/courses")
    //
    // public ResponseEntity<CourseDto> postCourse(Principal principal, @RequestBody
    // RequestCourseBody requestCourseBody) {

    // }

    // @PutMapping("/courses/{id}")
    //
    // esponseEntity<CourseDto> putCourse(Principal principal, @PathVariable UUID
    // id,
    // @RequestBody RequestCourseBody requestCourseBody) {

    // }

    // @DeleteMapping("/courses/{id}")
    //
    // public ResponseEntity<UUID> deleteCourse(Principal principal, @PathVariable
    // UUID id) {

    // }

}
