package emil.find_course.services;

import emil.find_course.domains.dto.AuthResponse;
import emil.find_course.domains.entities.user.User;
import emil.find_course.domains.requestDto.UserLoginRequest;
import emil.find_course.domains.requestDto.UserRegisterRequest;

public interface AuthService {

    public AuthResponse registerUser(UserRegisterRequest userRegisterRequest);

    public AuthResponse loginUser(UserLoginRequest userLoginRequest);

    public AuthResponse validateEmail(User user, String token);

    public void resendConfirmEmail(User user);
}
