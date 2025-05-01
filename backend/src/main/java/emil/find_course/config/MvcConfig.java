// package emil.find_course.config;

// import java.nio.file.Path;
// import java.nio.file.Paths;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.springframework.context.annotation.Configuration;
// import
// org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
// import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// @Configuration
// public class MvcConfig implements WebMvcConfigurer {

// private static final Logger log = LoggerFactory.getLogger(MvcConfig.class);

// private final String storageLocation = "./uploads/images";
// private final Path imageUploadDir =
// Paths.get(storageLocation).toAbsolutePath();

// @Override
// public void addResourceHandlers(ResourceHandlerRegistry registry) {
// String imageUploadPath = imageUploadDir.toFile().getAbsolutePath();

// String resourceLocation = "file:" + imageUploadPath + "/";

// log.info("Configuring resource handler for /uploads/images/** mapping to {}",
// resourceLocation);

// registry.addResourceHandler("/uploads/images/**")
// .addResourceLocations(resourceLocation);
// }
// }
