package emil.find_course.services;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import emil.find_course.domains.entities.course.Course;
import emil.find_course.domains.requestDto.course.SectionRequest;

public interface SectionService {

    public void syncSections(Course course, List<SectionRequest> sectionRequests);

}
