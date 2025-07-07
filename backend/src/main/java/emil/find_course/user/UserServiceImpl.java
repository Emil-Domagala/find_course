package emil.find_course.user;

import java.io.InputStream;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import emil.find_course.common.service.FileStorageService;
import emil.find_course.course.entity.Course;
import emil.find_course.user.dto.request.RequestUpdateUser;
import emil.find_course.user.entity.User;
import emil.find_course.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final FileStorageService fileStorageService;

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    @Override
    public User updateUser(User user, RequestUpdateUser requestUpdateUser, MultipartFile imageFile) {

        user.setUsername(requestUpdateUser.getUsername());
        user.setUserLastname(requestUpdateUser.getUserLastname());

        if (requestUpdateUser.getPassword() != null
                && !requestUpdateUser.getPassword().isEmpty()
                && requestUpdateUser.getPassword().length() >= 6
                && requestUpdateUser.getPassword().length() <= 30) {

            user.setPassword(passwordEncoder.encode(requestUpdateUser.getPassword()));
        }

        if (requestUpdateUser.getDeleteImage().equals(true)) {
            fileStorageService.deleteImage(user.getImageUrl());
            user.setImageUrl(null);

        }
        if (imageFile != null && !imageFile.isEmpty() && !requestUpdateUser.getDeleteImage().equals(true)) {
            String oldImgUrl = user.getImageUrl();
            String oryginalName = imageFile.getOriginalFilename();
            InputStream resizedImage = fileStorageService.resizeImage(imageFile, 150, 1, 1, 51_200);
            String imgUrl = fileStorageService.saveProcessedImage(resizedImage, "Avatar", oryginalName);
            user.setImageUrl(imgUrl);
            if (oldImgUrl != null) {
                fileStorageService.deleteImage(oldImgUrl);
            }

        }

        return userRepository.save(user);

    }

    @Override
    public void deleteUser(User user) {
        List<Course> courses = user.getEnrollmentCourses().stream().toList();
        for (Course course : courses) {
            course.getStudents().remove(user);
        }

        user.getEnrollmentCourses().clear();
        user.getTeachingCourses().clear();

        userRepository.delete(user);
    }

}
