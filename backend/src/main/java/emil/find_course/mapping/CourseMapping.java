package emil.find_course.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import emil.find_course.domains.dto.CourseDto;
import emil.find_course.domains.dto.detailsProt.CourseDetailsProtectedDto;
import emil.find_course.domains.dto.detailsPub.CourseDetailsPublicDto;
import emil.find_course.domains.entities.course.Course;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CourseMapping {

    CourseDto toDto(Course course);

    @Mapping(target = "courseDto", source = ".") // Maps entire Course entity to CourseDto inside CourseDetailsPublicDto
    @Mapping(target = "sections", source = "sections")
    CourseDetailsPublicDto toPublicDto(Course course);

    @Mapping(target = "courseDto", source = ".") // Maps entire Course entity to CourseDto inside CourseDetailsPublicDto
    @Mapping(target = "sections", source = "sections")
    CourseDetailsProtectedDto toProtectedDto(Course course);

}
