// package emil.find_course.controllers;

// import java.security.Principal;
// import java.util.List;
// import java.util.UUID;

// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.DeleteMapping;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.PutMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RestController;

// import jakarta.websocket.server.PathParam;

// @RestController("/api/v1")
// public class courseController {

//     @GetMapping("/courses")
//     public ResponseEntity<List<CourseDto>> getCourses() {
//         return "courses";
//     }

//     @GetMapping("/courses/{id}")
//     public ResponseEntity<CourseDto> getCourse(@PathVariable UUID id) {

//     }

//     @PostMapping("/courses")
//     public ResponseEntity<CourseDto> postCourse(Principal principal, @RequestBody RequestCourseBody requestCourseBody) {

//     }

//     @PutMapping("/courses/{id}")
//     public ResponseEntity<CourseDto> putCourse(Principal principal, @PathVariable UUID id,
//             @RequestBody RequestCourseBody requestCourseBody) {

//     }

//     @DeleteMapping("/courses/{id}")
//     public ResponseEntity<UUID> deleteCourse(Principal principal, @PathVariable UUID id) {

//     }

// }
