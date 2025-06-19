package emil.find_course.payment.stripe;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import emil.find_course.cart.CartService;
import emil.find_course.cart.entity.Cart;
import emil.find_course.common.security.jwt.UserDetailsImpl;
import emil.find_course.payment.stripe.dto.PaymentIntentResponse;
import emil.find_course.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Stripe Controller", description = "Endpoints for stripe")
@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
public class StripeController {

    private final CartService cartService;
    private final StripeService stripeService;

    @Operation(summary = "Create payment intent")
    @PostMapping("transaction/stripe/create-payment-intent")
    public ResponseEntity<PaymentIntentResponse> createPaymentIntent(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        final User user = userDetails.getUser();
        Cart cart = cartService.findByUserWithItemsAndCourses(user);

        PaymentIntentResponse response = stripeService.createPaymentIntent(cart, user);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Handle stripe webhook")
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
