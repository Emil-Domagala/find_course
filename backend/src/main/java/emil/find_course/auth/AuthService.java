package emil.find_course.auth;

import emil.find_course.auth.dto.request.UserLoginRequest;
import emil.find_course.auth.dto.request.UserRegisterRequest;
import emil.find_course.auth.dto.response.AuthResponse;
import emil.find_course.user.entity.User;

public interface AuthService {

    public User registerUser(UserRegisterRequest userRegisterRequest);

    public AuthResponse loginUser(UserLoginRequest userLoginRequest);

    public String refreshAuthToken(String token);

}
