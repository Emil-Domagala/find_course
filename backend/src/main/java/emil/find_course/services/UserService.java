package emil.find_course.services;

import emil.find_course.domains.entities.user.User;
import emil.find_course.domains.requestDto.UserRegisterRequest;

public interface UserService {

    public User registerUser(UserRegisterRequest userRegisterRequest);

}
