package emil.find_course.auth.confirmEmail.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RequestConfirmEmailOTT {

    @NotBlank(message = "Token is required")
    @Size(min = 6, max = 6, message = "Token must be exactly 6 characters long")
    private String token;
}
