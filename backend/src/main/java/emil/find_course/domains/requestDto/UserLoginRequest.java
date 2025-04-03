package emil.find_course.domains.requestDto;

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
