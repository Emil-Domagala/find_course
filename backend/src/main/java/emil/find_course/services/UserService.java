package emil.find_course.services;

import java.security.Principal;

import emil.find_course.domains.entities.user.User;
import emil.find_course.domains.requestDto.RequestUpdateUser;

public interface UserService {

    User findByEmail(String email);

    String getRoles(Principal principal);

    User updateUser(RequestUpdateUser requestUpdateUser, User user);

}