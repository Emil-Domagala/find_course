package emil.find_course.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.zaxxer.hikari.HikariDataSource;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class DataSourceConfig {

    @Value("${db.url}")
    private String dbUrl;

    @Value("${db.username}")
    private String dbUsername;

    @Value("${db.password}")
    private String dbPassword;

    @Value("${db.driver.class.name}")
    private String dbDriverClassName;

    @Bean
    @Primary
    public DataSource dataSource() {
        log.info("Configuring DataSource using .env variables...");

        if (dbUrl == null || dbUsername == null || dbPassword == null) {
            log.error("Database configuration properties (DB_URL, DB_USERNAME, DB_PASSWORD) are missing in .env file!");
            throw new IllegalStateException("Missing database configuration in .env file.");
        }

        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(dbUrl);
        dataSource.setUsername(dbUsername);
        dataSource.setPassword(dbPassword);
        dataSource.setDriverClassName(dbDriverClassName);

        log.info("HikariDataSource configured for URL: {}", dbUrl.split("@")[0]);
        return dataSource;
    }
}
