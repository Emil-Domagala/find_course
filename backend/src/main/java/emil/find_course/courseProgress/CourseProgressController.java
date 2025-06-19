package emil.find_course.courseProgress;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import emil.find_course.common.security.jwt.UserDetailsImpl;
import emil.find_course.courseProgress.dto.CourseProgressDto;
import emil.find_course.courseProgress.dto.request.UpdateProgressRequest;
import emil.find_course.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Course Progress Controller", description = "Endpoints for Course Progress")
@RestController
@RequestMapping("/api/v1/progress")
@RequiredArgsConstructor
public class CourseProgressController {

    private final CourseProgressService courseProgressService;

    @Operation(summary = "Get Course Progress", description = "Get Course Progress if not exists creates new one or if out of sync updates it")
    @GetMapping("/{courseId}")
    public ResponseEntity<CourseProgressDto> getProgress(@PathVariable UUID courseId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        final User user = userDetails.getUser();
        CourseProgressDto courseProgressDto = courseProgressService.getCourseProgress(courseId, user);
        return ResponseEntity.ok(courseProgressDto);

    }

    @Operation(summary = "Update Course Progress", description = "Update Course Progress mannually by user. It mark chapter as seen")
    @PatchMapping("/{courseId}")
    public ResponseEntity<Void> updateProgress(@PathVariable UUID courseId,
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody UpdateProgressRequest request) {

        final User user = userDetails.getUser();
        courseProgressService.updateChapterProgress(courseId, user, request);

        return ResponseEntity.noContent().build();
    }

}
