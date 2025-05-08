package emil.find_course.controllers;

import java.util.Map;

import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.stripe.model.PaymentIntent;

import emil.find_course.domains.dto.TransactionDto;
import emil.find_course.domains.entities.Cart;
import emil.find_course.domains.entities.user.User;
import emil.find_course.domains.pagination.PaginationRequest;
import emil.find_course.domains.pagination.PagingResult;
import emil.find_course.security.jwt.UserDetailsImpl;
import emil.find_course.services.CartService;
import emil.find_course.services.StripeService;
import emil.find_course.services.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;
    private final CartService cartService;
    private final StripeService stripeService;

    @PostMapping("transaction/stripe/create-payment-intent")
    public ResponseEntity<Map<String, String>> createPaymentIntent(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        final User user = userDetails.getUser();
        Cart cart = cartService.getCartByUser(user);

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

    @GetMapping("transaction")
    public ResponseEntity<PagingResult<TransactionDto>> getTransactions(@RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sortField,
            @RequestParam(required = false) Sort.Direction direction,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (size > 100) {
            size = 100;
        }
        final User user = userDetails.getUser();

        final PaginationRequest request = new PaginationRequest(page, size, sortField, direction);
        final PagingResult<TransactionDto> transactions = transactionService.getTransaction(user, request);

        return ResponseEntity.ok(transactions);
    }

}
