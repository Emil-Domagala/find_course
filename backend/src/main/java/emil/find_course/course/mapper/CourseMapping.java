package emil.find_course.course.mapper;

import java.util.Set;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import emil.find_course.course.dto.CourseDto;
import emil.find_course.course.dto.prot.CourseDetailsProtectedDto;
import emil.find_course.course.dto.pub.CourseDetailsPublicDto;
import emil.find_course.course.entity.Course;
import emil.find_course.course.section.mapper.SectionMapping;
import emil.find_course.courseProgress.dto.CourseStructure;
import emil.find_course.user.entity.User;
import emil.find_course.user.mapper.UserMapper;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = { SectionMapping.class,
        UserMapper.class })
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
