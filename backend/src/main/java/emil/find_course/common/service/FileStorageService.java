package emil.find_course.common.service;

import java.io.InputStream;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

    String saveProcessedImage(InputStream inputStream, String identifier, String originalFilenameBase);

    void deleteImage(String imageUrl);

    InputStream resizeImage(MultipartFile file, int targetWidth, int aspectRatioWidth, int aspectRatioHeight,
            long maxSizeBytes);
}
