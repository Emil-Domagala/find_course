package emil.find_course.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import emil.find_course.domains.dto.CourseDto;
import emil.find_course.domains.entities.course.Course;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CourseMapping {

    @Mapping(target = "teacher", source = "teacher")
    CourseDto toDto(Course course);
}
