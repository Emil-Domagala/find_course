package emil.find_course.controllers;

import java.security.Principal;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stripe.model.PaymentIntent;

import emil.find_course.domains.entities.Cart;
import emil.find_course.domains.entities.user.User;
import emil.find_course.services.CartService;
import emil.find_course.services.StripeService;
import emil.find_course.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
public class StripeController {

    private final UserService userService;
    private final CartService cartService;
    private final StripeService stripeService;

    @PostMapping("transaction/stripe/create-payment-intent")
    public ResponseEntity<Map<String, String>> createPaymentIntent(Principal principal) {
        User user = userService.findByEmail(principal.getName());
        Cart cart = cartService.getCartByUser(user);

        PaymentIntent intent = stripeService.createPaymentIntent(cart, user);
        System.out.println(intent.toString());
        Map<String, String> response = Map.of("clientSecret", intent.getClientSecret());
        return ResponseEntity.ok(response);

    }

    @PostMapping("public/transaction/stripe/webhook")
    public ResponseEntity<String> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) { // Get signature from header

        // try {
        // stripeService.handleWebhookEvent(payload, sigHeader);
        return ResponseEntity.ok().build(); // Return 200 OK to Stripe
        // } catch (StripeException e) {
        // // Logged within the service, return appropriate status code
        // log.error("StripeException in webhook handler: {}", e.getMessage());
        // if (e.getStatusCode() == 400) {
        // return ResponseEntity.badRequest().body("Webhook Error: " + e.getMessage());
        // } else {
        // return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Webhook
        // Error: " + e.getMessage());
        // }
        // } catch (Exception e) {
        // log.error("Unexpected error in webhook handler: {}", e.getMessage(), e);
        // return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal
        // Server Error");
        // }
    }

}
