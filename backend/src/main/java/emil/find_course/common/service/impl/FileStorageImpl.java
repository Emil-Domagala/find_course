package emil.find_course.common.service.impl;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import emil.find_course.common.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.Thumbnails.Builder;
import net.coobird.thumbnailator.geometry.Positions;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileStorageImpl implements FileStorageService {

    @Value("${bucket.name}")
    private String BUCKET_NAME;

    @Value("${r2.public.base.url}")
    private String R2_PUBLIC_BASE_URL;

    private final S3Client s3Client;

    private static final String OUTPUT_FORMAT = "jpeg";

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
        String cleanedBaseName = originalFilenameBase.replaceAll("[^a-zA-Z0-9_\\-]", "_");
        String timestamp = LocalDateTime.now().format(FILENAME_DATE_FORMATTER);

        String objectKey = String.format("%s_%s_%s.%s",
                sanitizedIdentifier,
                timestamp,
                cleanedBaseName,
                OUTPUT_FORMAT);
        String fullUrl = null;
        try {
            byte[] imageBytes = inputStream.readAllBytes();
            long contentLength = imageBytes.length;
            String contentType = "image/jpeg";

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(BUCKET_NAME)
                    .key(objectKey)
                    .contentType(contentType)
                    .contentLength(contentLength)
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(imageBytes));
            log.info("Successfully uploaded object '{}' to R2 bucket '{}'. Size: {} bytes.", objectKey,
                    BUCKET_NAME,
                    contentLength);

            String baseUrl = R2_PUBLIC_BASE_URL.endsWith("/") ? R2_PUBLIC_BASE_URL : R2_PUBLIC_BASE_URL + "/";
            String keyForUrl = objectKey.startsWith("/") ? objectKey.substring(1) : objectKey;
            return fullUrl = baseUrl + BUCKET_NAME + "/" + keyForUrl;

        } catch (IOException e) {
            log.error("Failed to read processed image stream for R2 upload", e);
            throw new RuntimeException("Failed to read processed image stream", e);
        } catch (S3Exception e) {
            log.error("S3Exception during R2 upload for key {}: {} - {}", objectKey, e.statusCode(), e.getMessage(), e);
            throw new RuntimeException("Failed to upload image to R2 storage: " + e.getMessage(), e);
        } catch (SdkException e) {
            log.error("SdkException during R2 upload for key {}: {}", objectKey, e.getMessage(), e);
            throw new RuntimeException("Failed to upload image to R2 storage (SDK Error): " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error saving processed image stream to R2 for key {}", objectKey, e);
            throw new RuntimeException("Unexpected error saving processed image stream to R2", e);
        } finally {
            try {
                inputStream.close(); // Ensure the input stream is closed
            } catch (IOException e) {
                log.warn("Failed to close input stream after R2 upload attempt", e);
            }
        }
    }

    @Override
    public void deleteImage(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) {
            return;
        }

        String objectKey;
        try {
            objectKey = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);

            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(BUCKET_NAME)
                    .key(objectKey)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);

        } catch (S3Exception e) {
            if (e.statusCode() == 404) {
                log.warn("Attempted to delete non-existent object from R2 bucket");
            } else {
                log.error("S3Exception during R2 delete for key");
            }
        } catch (SdkException e) {
            log.error("AWS SDKException during R2 delete for key");
        } catch (Exception e) {
            log.error("Unexpected error deleting object from R2");
        }
    }

    @Override
    public InputStream resizeImage(MultipartFile file, int targetWidth, int aspectRatioWidth, int aspectRatioHeight,
            long maxSizeBytes) {
        final double ASPECT_RATIO_TOLERANCE = 0.05;
        final double MIN_QUALITY = 0.1;

        boolean needsProcessing = false;
        byte[] originalImageBytes;

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
            double targetAspectRatio = (double) aspectRatioWidth / aspectRatioHeight;

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
                processingHeight = (int) Math.round((double) processingWidth * aspectRatioHeight / aspectRatioWidth);
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

            if (currentSize > maxSizeBytes) {
                log.warn("Image size ({}) exceeds limit ({} bytes). Reducing quality.", currentSize, maxSizeBytes);

                double qualityFactor = (double) maxSizeBytes / currentSize;
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

                if (finalSize > maxSizeBytes) {
                    log.error(
                            "Image size ({}) still exceeds limit ({}) even after reducing quality to {}. Cannot process further.",
                            finalSize, maxSizeBytes, String.format("%.3f", qualityFactor));
                    throw new RuntimeException(
                            "Image could not be reduced to the required size limit (" + (maxSizeBytes / 1024)
                                    + " KB).");
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
