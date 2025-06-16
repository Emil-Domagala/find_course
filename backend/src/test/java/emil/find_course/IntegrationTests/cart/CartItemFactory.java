package emil.find_course.IntegrationTests.cart;

import emil.find_course.cart.entity.Cart;
import emil.find_course.cart.entity.CartItem;
import emil.find_course.course.entity.Course;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class CartItemFactory {

    public static CartItem createCartItem(Course course, Cart cart) {
        return CartItem.builder().course(course).cart(cart).build();
    }

}
