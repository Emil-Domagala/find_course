package emil.find_course.auth.resetPassword;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import emil.find_course.auth.resetPassword.dto.request.ResetPasswordEmailRequest;
import emil.find_course.auth.resetPassword.dto.request.ResetPasswordPasswordRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Reset Password Controller", description = "Endpoints for reset password")
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ResetPasswordController {

    private final ResetPasswordService resetPasswordService;

    @Operation(summary = "Send email to reset password")
    @PostMapping("/public/forgot-password")
    public ResponseEntity<Void> sendResetPasswordEmail(@Validated @RequestBody ResetPasswordEmailRequest email) {
        resetPasswordService.sendResetPasswordEmail(email.getEmail());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Reset password")
    @PostMapping("/public/reset-password")
    public ResponseEntity<Void> resetPassword(@RequestParam(required = false) String token,
            @Validated @RequestBody ResetPasswordPasswordRequest password) {
        if (token == null) {
            throw new IllegalArgumentException("Token cannot be null");
        }

        resetPasswordService.resetPassword(token, password.getPassword());
        return ResponseEntity.noContent().build();
    }

}
