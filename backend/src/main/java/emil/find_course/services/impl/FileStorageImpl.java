package emil.find_course.services.impl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.sql.Date;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import emil.find_course.services.FileStorageService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j; // Added for logging

@Service
@Slf4j
public class FileStorageImpl implements FileStorageService {

    private String storageLocation = "./uploads/images";
    private Path rootLocation;

    @PostConstruct
    public void init() {
        try {
            rootLocation = Paths.get(storageLocation).toAbsolutePath().normalize();
            log.info("Initializing storage at: {}", rootLocation);
            Files.createDirectories(rootLocation);
            log.info("Storage directory ensured: {}", rootLocation);
        } catch (IOException e) {
            log.error("Could not initialize storage location: {}", storageLocation, e);
            throw new RuntimeException("Could not initialize storage location: " + storageLocation, e);
        }
    }

    @Override
    public String saveImage(MultipartFile file, String identifier) {

        System.out.println("Trying to upload img");
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be null or empty.");
        }
        if (identifier == null || identifier.isBlank()) {
            throw new IllegalArgumentException("Identifier cannot be empty or null.");
        }

        String sanitizedIdentifier = identifier.replaceAll("[^a-zA-Z0-9_\\-]", "_");
        Path userDirectory = this.rootLocation.resolve(sanitizedIdentifier);
        System.out.println(userDirectory);
        try {
            Files.createDirectories(userDirectory);
            String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
            String filename = sanitizedIdentifier + "_" + LocalDateTime.now().toString().replaceAll("[^a-zA-Z0-9_\\-]",
                    "-") + "_" + originalFilename;
            Path destinationFile = userDirectory.resolve(filename).normalize().toAbsolutePath();
            if (!destinationFile.getParent().equals(userDirectory.toAbsolutePath())) {
                log.error("Cannot store file outside target directory structure. Identifier: {}, Filename: {}",
                        identifier, originalFilename);
                throw new RuntimeException("Cannot store file outside target directory structure.");
            }

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }

            String relativePath = sanitizedIdentifier + "/" + filename;
            log.info("File saved successfully. Relative path: {}", relativePath);
            return relativePath;

        } catch (IOException e) {
            log.error("Failed to store file {} for identifier {}: {}", file.getOriginalFilename(), identifier,
                    e.getMessage(), e);
            throw new RuntimeException("Failed to store file " + file.getOriginalFilename(), e);
        }
    }

    @Override
    public void deleteImage(String relativePath) {
        if (relativePath == null || relativePath.isBlank()) {
            log.warn("Attempted to delete image with empty relative path.");
            return;
        }

        try {
            Path filePath = this.rootLocation.resolve(relativePath).normalize();

            if (!filePath.startsWith(this.rootLocation)) {
                log.error("Attempt to delete file outside storage root: {}", relativePath);
                throw new SecurityException("Cannot delete file outside storage root.");
            }

            Files.deleteIfExists(filePath);

        } catch (IOException e) {
            log.error("Could not delete file {}: {}", relativePath, e.getMessage(), e);
        } catch (InvalidPathException e) {
        }
    }

}
