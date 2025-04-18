package emil.find_course.services;

import com.stripe.model.PaymentIntent;

import emil.find_course.domains.entities.Cart;
import emil.find_course.domains.entities.user.User;

public interface StripeService {

    public PaymentIntent createPaymentIntent(Cart cart, User user);

    public void handleWebhookEvent(String payload, String sigHeader);


}
