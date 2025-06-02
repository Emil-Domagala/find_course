package emil.find_course.course.section;

import java.util.List;

import emil.find_course.course.entity.Course;
import emil.find_course.course.section.dto.request.SectionRequest;

public interface SectionService {

    public void syncSections(Course course, List<SectionRequest> sectionRequests);

}
