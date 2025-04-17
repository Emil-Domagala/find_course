package emil.find_course.controllers;

import java.security.Principal;
import java.util.Map;

import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stripe.model.PaymentIntent;

import emil.find_course.domains.dto.CustomPaymentIntent;
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
        Map<String, String> response = Map.of("clientSecret", intent.getClientSecret());
        return ResponseEntity.ok(response);

    }

    @PostMapping("public/transaction/stripe/webhook")
    public ResponseEntity<String> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) { // Get signature from header

        stripeService.handleWebhookEvent(payload, sigHeader);
        return ResponseEntity.ok().build();
    }

    @Profile("dev")
    @PostMapping("transaction/stripe/finalize-payment")
    public ResponseEntity<Void> handleTarnsaction(Principal principal, @RequestBody CustomPaymentIntent paymentIntent) {
        System.out.println("PROCEDE TO SAVE TRANSACTION IN DEV MODE");
        System.out.println("||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||");
        User user = userService.findByEmail(principal.getName());
        stripeService.handleTarnsaction(user, paymentIntent);
        return ResponseEntity.ok().build();
    }

}
