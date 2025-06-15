package emil.find_course.cart.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import emil.find_course.cart.dto.CartDto;
import emil.find_course.cart.entity.Cart;
import emil.find_course.course.mapper.CourseMapper;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = { CourseMapper.class })
public interface CartMapper {

    CartDto toDto(Cart cart);
}
