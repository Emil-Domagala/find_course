package emil.find_course.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import emil.find_course.domains.dto.UserDto;
import emil.find_course.domains.entities.user.User;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapping {

    UserDto toDto(User user);

}
