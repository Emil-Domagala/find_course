package emil.find_course.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import emil.find_course.user.dto.UserDto;
import emil.find_course.user.entity.User;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    UserDto toDto(User user);

}
