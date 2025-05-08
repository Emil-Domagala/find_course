package emil.find_course.controllers;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import emil.find_course.domains.dto.courseProgress.CourseProgressDto;

import emil.find_course.domains.entities.user.User;
import emil.find_course.domains.requestDto.UpdateProgressRequest;
import emil.find_course.security.jwt.UserDetailsImpl;
import emil.find_course.services.CourseProgressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/progress")
@RequiredArgsConstructor
public class CourseProgressController {

    private final CourseProgressService courseProgressService;

    @GetMapping("/{courseId}")
    public ResponseEntity<CourseProgressDto> getProgress(@PathVariable UUID courseId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        final User user = userDetails.getUser();
        CourseProgressDto courseProgressDto = courseProgressService.getCourseProgress(courseId, user);
        return ResponseEntity.ok(courseProgressDto);

    }

    @PatchMapping("/{courseId}")
    public ResponseEntity<Void> updateProgress(@PathVariable UUID courseId,
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody UpdateProgressRequest request) {

        final User user = userDetails.getUser();
        courseProgressService.updateChapterProgress(courseId, user, request);

        return ResponseEntity.ok().build();
    }

}
