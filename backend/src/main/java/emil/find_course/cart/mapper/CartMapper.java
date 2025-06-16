package emil.find_course.cart.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import emil.find_course.cart.dto.CartDto;
import emil.find_course.cart.entity.Cart;
import emil.find_course.cart.entity.CartItem;
import emil.find_course.course.dto.CourseDto;
import emil.find_course.course.mapper.CourseMapper;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = { CourseMapper.class })
public interface CartMapper {

    @Mapping(target = "courses", source = "cartItems")
    @Mapping(target = "totalPrice", expression = "java(calculateTotalPrice(cart))")
    CartDto toDto(Cart cart);

    @Mapping(target = ".", source = "course")
    CourseDto cartItemToCourseDto(CartItem cartItem);

    default int calculateTotalPrice(Cart cart) {
        if (cart == null || cart.getCartItems() == null) {
            return 0;
        }
        return cart.getCartItems().stream()
                .mapToInt(CartItem::getPriceAtAddition)
                .sum();
    }
}
