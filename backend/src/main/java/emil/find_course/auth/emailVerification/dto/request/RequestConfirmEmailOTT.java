package emil.find_course.auth.emailVerification.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RequestConfirmEmailOTT {

    @NotBlank(message = "Title is required")
    @Size(min = 6, max = 6, message = "Title must be exactly 6 characters long")
    private String token;
}
