package emil.find_course.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.cdimascio.dotenv.Dotenv;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class R2Config {
    Dotenv dotenv = Dotenv.load();

    private final String ACCESS_KEY_ID = dotenv.get("ACCESS_KEY_ID");
    private final String SECRET_ACCESS_KEY = dotenv.get("SECRET_ACCESS_KEY");
    private final String S3_API = dotenv.get("S3_API");

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
