package emil.find_course.user;

import java.util.Set;

import org.springframework.web.multipart.MultipartFile;

import emil.find_course.course.entity.Course;
import emil.find_course.user.dto.request.RequestUpdateUser;
import emil.find_course.user.entity.User;

public interface UserService {

    public User findByEmail(String email);

    public User updateUser(User user, RequestUpdateUser requestUpdateUser, MultipartFile imageFile);

    public void deleteUser(User user);

    public void grantAccessToCourse(User user, Set<Course> course);

}
