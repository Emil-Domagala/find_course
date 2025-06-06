package emil.find_course.auth.resetPassword.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class PasswordRequest {
    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 30, message = "Password must be at least {min} and {max} characters long")
    private String password;
}
