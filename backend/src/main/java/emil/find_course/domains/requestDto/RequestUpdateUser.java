package emil.find_course.domains.requestDto;

import io.micrometer.common.lang.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RequestUpdateUser {

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 30, message = "Username must be between {min} and {max} characters")
    private String username;

    @NotBlank(message = "Lastname is required")
    @Size(min = 2, max = 30, message = "Lastname must be between {min} and {max} characters")
    private String userLastname;

    @Nullable
    private boolean deleteImage;

    @Size(min = 6, max = 30, message = "Password must be at least {min} and {max} characters long")
    @Nullable
    private String password;
}
