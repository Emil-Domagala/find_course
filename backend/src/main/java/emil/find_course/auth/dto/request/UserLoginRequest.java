package emil.find_course.auth.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserLoginRequest {
    private String email;
    private String password; 
}
