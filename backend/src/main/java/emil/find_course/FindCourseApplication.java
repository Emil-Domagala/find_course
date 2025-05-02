package emil.find_course;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableAsync
@SpringBootApplication
public class FindCourseApplication {

	public static void main(String[] args) {
		System.out.println("Find Course Version 1");
		SpringApplication.run(FindCourseApplication.class, args);
	}

}
