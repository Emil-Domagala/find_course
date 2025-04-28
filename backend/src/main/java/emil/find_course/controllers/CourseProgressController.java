package emil.find_course.controllers;

import java.security.Principal;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import emil.find_course.services.UserService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/progress")
@RequiredArgsConstructor
public class CourseProgressController {

     private final UserService userService;

    @GetMapping("/{courseId}")
    public ResponseEntity<?> getProgress(@PathVariable UUID courseId, Principal principal) {
        return null;
    }
    @PatchMapping("/{courseId}")
    public ResponseEntity<?> updateProgress(@PathVariable UUID courseId, Principal principal) {
        return null;
    }

}
