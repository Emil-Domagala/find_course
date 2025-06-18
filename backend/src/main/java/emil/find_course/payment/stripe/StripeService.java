package emil.find_course.payment.stripe;

import emil.find_course.cart.entity.Cart;
import emil.find_course.payment.stripe.dto.PaymentIntentResponse;
import emil.find_course.user.entity.User;

public interface StripeService {

    public PaymentIntentResponse createPaymentIntent(Cart cart, User user);

    public void handleWebhookEvent(String payload, String sigHeader);

}
