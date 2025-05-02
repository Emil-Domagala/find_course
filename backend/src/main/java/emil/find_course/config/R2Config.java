package emil.find_course.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class R2Config {

    @Value("${access.key.id}")
    private String ACCESS_KEY_ID;

    @Value("${secret.access.key}")
    private String SECRET_ACCESS_KEY;

    @Value("${s3.api}")
    private String S3_API;

    @Bean
    public S3Client r2S3Client() {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(ACCESS_KEY_ID, SECRET_ACCESS_KEY);
        return S3Client.builder()
                .region(Region.of("auto"))
                .credentialsProvider(() -> credentials)
                .endpointOverride(java.net.URI.create(
                        S3_API))
                .build();

    }
}
