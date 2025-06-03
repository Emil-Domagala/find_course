package emil.find_course.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisterRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 30, message = "Username must be between {min} and {max} characters")
    private String username;

    @NotBlank(message = "Lastname is required")
    @Size(min = 2, max = 30, message = "Lastname must be between {min} and {max} characters")
    private String userLastname;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 30, message = "Password must be at least {min} and {max} characters long")
    private String password;

}
