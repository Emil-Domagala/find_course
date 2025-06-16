package emil.find_course.payment.stripe;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stripe.model.PaymentIntent;

import emil.find_course.cart.CartService;
import emil.find_course.cart.entity.Cart;
import emil.find_course.common.security.jwt.UserDetailsImpl;
import emil.find_course.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
public class StripeController {

    private final CartService cartService;
    private final StripeService stripeService;

    @PostMapping("transaction/stripe/create-payment-intent")
    public ResponseEntity<Map<String, String>> createPaymentIntent(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        final User user = userDetails.getUser();
        Cart cart = cartService.findByUserWithItemsAndCourses(user);

        // TODO: If some courses were deleted from cart inform user
        PaymentIntent intent = stripeService.createPaymentIntent(cart, user);
        Map<String, String> response = Map.of("clientSecret", intent.getClientSecret());
        return ResponseEntity.ok(response);
    }

    @PostMapping("public/transaction/stripe/webhook")
    public ResponseEntity<String> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {
        // stripe listen --forward-to
        // localhost:8080/api/v1/public/transaction/stripe/webhook

        stripeService.handleWebhookEvent(payload, sigHeader);
        return ResponseEntity.ok().build();
    }
}
