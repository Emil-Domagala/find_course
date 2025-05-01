package emil.find_course.mapping;

import java.util.Set;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import emil.find_course.domains.dto.course.CourseDto;
import emil.find_course.domains.dto.courseProgress.CourseStructure;
import emil.find_course.domains.dto.detailsProt.CourseDetailsProtectedDto;
import emil.find_course.domains.dto.detailsPub.CourseDetailsPublicDto;
import emil.find_course.domains.entities.course.Course;
import emil.find_course.domains.entities.user.User;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = { SectionMapping.class,
        UserMapping.class })
public interface CourseMapping {

    @Mapping(target = "studentsCount", source = "students", qualifiedByName = "calculateEnrolledStudents")
    CourseDto toDto(Course course);

    @Mapping(target = "sections", source = "sections")
    CourseDetailsPublicDto toPublicDto(Course course);

    @Mapping(target = "sections", source = "sections")
    CourseDetailsProtectedDto toProtectedDto(Course course);

    CourseStructure toCourseStructure(Course course);

    @Named("calculateEnrolledStudents")
    default long calculateEnrolledStudents(Set<User> students) {

        if (students == null) {
            return 0;
        }
        return students.stream().count();
    }
}
