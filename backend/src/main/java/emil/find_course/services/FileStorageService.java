package emil.find_course.services;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    String saveImage(MultipartFile file, String identifier);
    void deleteImage(String imageUrl);
}
