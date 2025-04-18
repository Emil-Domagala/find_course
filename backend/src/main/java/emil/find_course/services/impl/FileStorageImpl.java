package emil.find_course.services.impl;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import javax.imageio.ImageIO;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import emil.find_course.services.FileStorageService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.Thumbnails.Builder;
import net.coobird.thumbnailator.geometry.Positions;

@Service
@Slf4j
public class FileStorageImpl implements FileStorageService {
    private static final String OUTPUT_FORMAT = "jpg";
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
    public String saveProcessedImage(InputStream inputStream, String identifier, String originalFilenameBase) {
        final DateTimeFormatter FILENAME_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
        Objects.requireNonNull(inputStream, "InputStream cannot be null.");
        Objects.requireNonNull(identifier, "Identifier cannot be null or blank.");
        Objects.requireNonNull(originalFilenameBase, "Original filename base cannot be null.");

        if (identifier.isBlank()) {
            throw new IllegalArgumentException("Identifier cannot be blank.");
        }

        String sanitizedIdentifier = identifier.replaceAll("[^a-zA-Z0-9_\\-]", "_");
        Path userDirectory = this.rootLocation.resolve(sanitizedIdentifier);

        try {
            Files.createDirectories(userDirectory);

            String cleanedBaseName = originalFilenameBase.replaceAll("[^a-zA-Z0-9_\\-]", "_");

            String timestamp = LocalDateTime.now().format(FILENAME_DATE_FORMATTER);
            String filename = String.format("%s_%s_%s.%s",
                    sanitizedIdentifier,
                    timestamp,
                    cleanedBaseName,
                    OUTPUT_FORMAT);

            Path destinationFile = userDirectory.resolve(filename).normalize().toAbsolutePath();

            if (!destinationFile.getParent().equals(userDirectory.toAbsolutePath())) {
                log.error(
                        "Security risk: Cannot store processed file outside target directory structure. Identifier: {}, Filename: {}",
                        identifier, filename);
                throw new RuntimeException("Cannot store file outside target directory structure.");
            }

            log.debug("Target destination file for processed image: {}", destinationFile);

            Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);

            String relativePath = sanitizedIdentifier + "/" + filename;
            log.info("Processed image saved successfully. Relative path: {}", relativePath);
            return relativePath;

        } catch (IOException e) {
            log.error("Failed to save processed image stream for identifier {}: {}", identifier, e.getMessage(), e);
            throw new RuntimeException("Failed to save processed image stream", e);
        } catch (Exception e) {
            log.error("Unexpected error saving processed image stream for identifier {}: {}", identifier,
                    e.getMessage(), e);
            throw new RuntimeException("Unexpected error saving processed image stream", e);
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

    @Override
    public InputStream resizeImage(MultipartFile file, int targetWidth, int aspectRatioLeft, int aspectRatioRight,
            long maxSize) {
        final double ASPECT_RATIO_TOLERANCE = 0.05;
        final double MIN_QUALITY = 0.1;

        boolean needsProcessing = false;
        byte[] originalImageBytes;

        System.out.println("In resizing method");

        try {
            originalImageBytes = file.getBytes();
            if (originalImageBytes.length == 0) {
                throw new IllegalArgumentException("Input file is empty.");
            }
            BufferedImage image;
            try (InputStream dimCheckStream = new ByteArrayInputStream(originalImageBytes)) {
                image = ImageIO.read(dimCheckStream);
                if (image == null) {
                    throw new RuntimeException("Could not read image data. Is it a valid image format?");
                }
            }

            int originalWidth = image.getWidth();
            int originalHeight = image.getHeight();
            double originalAspectRatio = (double) originalWidth / originalHeight;
            double targetAspectRatio = (double) aspectRatioLeft / aspectRatioRight;

            log.debug("Original Dims: {}x{}, Aspect Ratio: {}", originalWidth, originalHeight,
                    String.format("%.3f", originalAspectRatio));
            log.debug("Target Width: {}, Target Aspect Ratio: {}", targetWidth,
                    String.format("%.3f", targetAspectRatio));

            if (originalWidth > targetWidth) {
                log.info("Image width ({}) exceeds target width ({}). Needs processing.", originalWidth, targetWidth);
                needsProcessing = true;
            } else if (Math.abs(originalAspectRatio - targetAspectRatio) > ASPECT_RATIO_TOLERANCE) {
                log.info("Image aspect ratio ({}) differs significantly from target ({}). Needs processing.",
                        String.format("%.3f", originalAspectRatio), String.format("%.3f", targetAspectRatio));
                needsProcessing = true;
            } else {
                log.info("Image dimensions and aspect ratio are within limits. No resize/crop needed initially.");
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            int processingWidth = originalWidth;
            int processingHeight = originalHeight;

            if (needsProcessing) {
                processingWidth = targetWidth;
                processingHeight = (int) Math.round((double) processingWidth * aspectRatioRight / aspectRatioLeft);
                if (processingHeight <= 0) {
                    throw new IllegalArgumentException("Calculated processing height is invalid.");
                }
                log.debug("Processing dimensions set to: {}x{}", processingWidth, processingHeight);
            }

            try (InputStream firstPassStream = new ByteArrayInputStream(originalImageBytes)) {
                Builder<? extends InputStream> builder = Thumbnails.of(firstPassStream);
                if (needsProcessing) {
                    builder = builder.size(processingWidth, processingHeight).crop(Positions.CENTER);
                } else {
                    builder = builder.scale(1.0);
                }
                builder.outputFormat(OUTPUT_FORMAT)
                        .outputQuality(0.95)
                        .toOutputStream(outputStream);
            }

            byte[] currentBytes = outputStream.toByteArray();
            long currentSize = currentBytes.length;
            log.debug("Image size after first pass: {} bytes", currentSize);

            if (currentSize > maxSize) {
                log.warn("Image size ({}) exceeds limit ({} bytes). Reducing quality.", currentSize, maxSize);

                double qualityFactor = (double) maxSize / currentSize;
                qualityFactor = Math.sqrt(qualityFactor);
                qualityFactor = Math.max(MIN_QUALITY, Math.min(qualityFactor, 0.95));

                log.info("Applying reduced quality factor: {}", String.format("%.3f", qualityFactor));

                outputStream.reset();

                try (InputStream secondPassStream = new ByteArrayInputStream(originalImageBytes)) {
                    Builder<? extends InputStream> builder = Thumbnails.of(secondPassStream);
                    if (needsProcessing) {
                        builder = builder.size(processingWidth, processingHeight).crop(Positions.CENTER);
                    } else {
                        builder = builder.scale(1.0);
                    }
                    builder.outputFormat(OUTPUT_FORMAT)
                            .outputQuality(qualityFactor)
                            .toOutputStream(outputStream);
                }

                currentBytes = outputStream.toByteArray();
                long finalSize = currentBytes.length;
                log.debug("Image size after quality reduction: {} bytes", finalSize);

                if (finalSize > maxSize) {
                    log.error(
                            "Image size ({}) still exceeds limit ({}) even after reducing quality to {}. Cannot process further.",
                            finalSize, maxSize, String.format("%.3f", qualityFactor));
                    throw new RuntimeException(
                            "Image could not be reduced to the required size limit (" + (maxSize / 1024) + " KB).");
                }
            } else {
                log.info("Image size is within limit. No quality reduction needed.");
            }

            log.info("Image processing finished successfully.");
            return new ByteArrayInputStream(currentBytes);

        } catch (IOException e) {
            log.error("IOException during image processing: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to read or process image file.", e);
        } catch (RuntimeException e) {
            log.error("Error during image processing: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during image resizing: {}", e.getMessage(), e);
            throw new RuntimeException("An unexpected error occurred during image processing.", e);
        }
    }

}
