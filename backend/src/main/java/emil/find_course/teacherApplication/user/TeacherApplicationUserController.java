package emil.find_course.teacherApplication.user;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import emil.find_course.common.security.jwt.UserDetailsImpl;
import emil.find_course.teacherApplication.dto.TeacherApplicationDto;
import emil.find_course.teacherApplication.entity.TeacherApplication;
import emil.find_course.teacherApplication.mapper.TeacherApplicationMapper;
import emil.find_course.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Teacher Application User Controller", description = "Endpoints for menaging teacher applications by user")
@RestController
@RequestMapping("/api/v1/user/teacher-application") 
@RequiredArgsConstructor
public class TeacherApplicationUserController {

    private final TeacherApplicationUserService teacherApplicationService;
    private final TeacherApplicationMapper teacherApplicationMapper;

    @Operation(summary = "Sends request to become a teacher")
    @PostMapping
    public ResponseEntity<Void> postTeacherApplication(@AuthenticationPrincipal UserDetailsImpl userDetails) {

        final User user = userDetails.getUser();
        teacherApplicationService.createTeacherApplication(user);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get user teacher application if exists")
    @GetMapping
    public ResponseEntity<TeacherApplicationDto> getUserTeacherApplication(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        final User user = userDetails.getUser();
        Optional<TeacherApplication> becomeTeacherOpt = teacherApplicationService.getUserTeacherApplication(user);
        TeacherApplicationDto becomeTeacherDto = becomeTeacherOpt.map(
                teacherApplicationMapper::toDto)
                .orElse(new TeacherApplicationDto());
        return ResponseEntity.ok(becomeTeacherDto);

    }

}
