package emil.find_course.services;

import java.security.Principal;

import emil.find_course.domains.entities.user.User;

public interface UserService {

    User findByEmail(String email);

    String getRoles(Principal principal);

}