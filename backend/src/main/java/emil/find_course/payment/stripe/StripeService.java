package emil.find_course.payment.stripe;

import com.stripe.model.PaymentIntent;

import emil.find_course.cart.entity.Cart;
import emil.find_course.user.entity.User;

public interface StripeService {

    public PaymentIntent createPaymentIntent(Cart cart, User user);

    public void handleWebhookEvent(String payload, String sigHeader);

}
