package emil.find_course.services.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.StripeObject;
import com.stripe.net.Webhook;
import com.stripe.param.PaymentIntentCreateParams;

import emil.find_course.domains.entities.Cart;
import emil.find_course.domains.entities.Transaction;
import emil.find_course.domains.entities.user.User;
import emil.find_course.exceptions.CustomStripeException;
import emil.find_course.services.CartService;
import emil.find_course.services.EmailService;
import emil.find_course.services.StripeService;
import emil.find_course.services.TransactionService;
import emil.find_course.services.UserService;
import io.github.cdimascio.dotenv.Dotenv;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class StripeServiceImpl implements StripeService {
    Dotenv dotenv = Dotenv.load();

    private final String webhookSecret = dotenv.get("STRIPE_WEBHOOK_SECRET");

    @Value("${frontend.domain}")
    private String frontendDomain;

    private final EmailService emailService;
    private final UserService userService;
    private final CartService cartService;
    private final TransactionService transactionService;

    @Override
    public PaymentIntent createPaymentIntent(Cart cart, User user) {
        if (cart.getTotalPrice() <= 0) {
            throw new IllegalArgumentException("Cart total price must be positive.");
        }

        Set<String> courseIds = cart.getCourses().stream()
                .map(course -> course.getId().toString())
                .collect(Collectors.toSet());

        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setReceiptEmail(user.getEmail())
                .setAmount((long) cart.getTotalPrice())
                .setCurrency("usd")
                .setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods.builder().setEnabled(true).build())
                .putMetadata("userId", user.getId().toString())
                .putMetadata("cartId", cart.getId().toString())
                .putMetadata("courseIds", String.join(",", courseIds))
                .setDescription("Course purchase for user " + user.getEmail())
                .build();

        try {
            PaymentIntent intent = PaymentIntent.create(params);
            return intent;
        } catch (StripeException e) {
            throw new CustomStripeException("Error creating PaymentIntent");
        } catch (Exception e) {
            throw new RuntimeException("Error creating PaymentIntent");
        }
    }

    @Override
    @Transactional
    public void handleWebhookEvent(String payload, String sigHeader) {
        Event event;

        try {
            // Verify the event signature using your webhook signing secret
            event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
        } catch (SignatureVerificationException e) {
            log.error("Webhook error: Invalid signature.");
            throw new CustomStripeException("Invalid Stripe signature");
        } catch (Exception e) {
            log.error("Webhook error: Could not construct event.");
            throw new CustomStripeException("Invalid payload");
        }

        EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
        StripeObject stripeObject = null;
        if (dataObjectDeserializer.getObject().isPresent()) {
            stripeObject = dataObjectDeserializer.getObject().get();
        } else {
            throw new CustomStripeException("Deserialization failed, probably due to an API version mismatch");
        }

        // Handle the event
        switch (event.getType()) {
            case "payment_intent.succeeded":
                PaymentIntent paymentIntent = (PaymentIntent) stripeObject;
                handlePaymentSuccess(paymentIntent);
                break;
            case "payment_intent.payment_failed":
                PaymentIntent failedPaymentIntent = (PaymentIntent) stripeObject;
                log.warn("Webhook received: PaymentIntent failed. ID: {}, Reason: {}",
                        failedPaymentIntent.getId(),
                        failedPaymentIntent.getLastPaymentError() != null
                                ? failedPaymentIntent.getLastPaymentError().getMessage()
                                : "Unknown");
                break;

            default:
                log.info("Webhook received: Unhandled event type: {}", event.getType());
        }
    }

    private void handlePaymentSuccess(PaymentIntent paymentIntent) {
        String userIdStr = paymentIntent.getMetadata().get("userId");
        String cartIdStr = paymentIntent.getMetadata().get("cartId");
        String courseIdsStr = paymentIntent.getMetadata().get("courseIds");

        if (userIdStr.isBlank() || cartIdStr.isBlank() || courseIdsStr.isBlank()) {
            log.error("Webhook error: Missing userId, cartId or courseIds in PaymentIntent metadata. ID: {}",
                    paymentIntent.getId());
            return;
        }

        try {
            User user = userService.findByEmail(paymentIntent.getReceiptEmail());

            if (transactionService.existsByPaymentIntentId(paymentIntent.getId())) {
                log.warn("Webhook warning: PaymentIntent {} already processed.", paymentIntent.getId());
                return;
            }
            Cart cart = cartService.getCartByUser(user);
            if (cart == null || cart.getCourses().isEmpty()) {
                log.error("Webhook error: Cart not found or empty for user {} during fulfillment for PaymentIntent {}",
                        paymentIntent.getId());
                return;
            }

            if (paymentIntent.getAmount() != (long) cart.getTotalPrice()) {
                log.error("Webhook error: Amount mismatch for PaymentIntent {}. Expected: {}, Actual: {}",
                        paymentIntent.getId(), cart.getTotalPrice(), paymentIntent.getAmount());
                return;
            }

            // !!!!!!!!!!!
            userService.grantAccessToCourse(user, cart.getCourses());
            Transaction savedTransaction = transactionService.createTransaction(user, paymentIntent, cart);
            sendPurchasedEmail(user, cart, savedTransaction);
            cartService.deleteCart(cart);
            // !!!!!!!!!!!!

        } catch (EntityNotFoundException e) {
            log.error("Webhook error: User not found for ID {} from PaymentIntent metadata. ID: {}", userIdStr,
                    paymentIntent.getId(), e);
        } catch (IllegalArgumentException e) {
            log.error("Webhook error: Invalid UUID format for userId {} from PaymentIntent metadata. ID: {}", userIdStr,
                    paymentIntent.getId(), e);
        } catch (Exception e) {
            log.error("Webhook error: Unexpected error during fulfillment for PaymentIntent {}.", paymentIntent.getId(),
                    e);
            throw new RuntimeException("Fulfillment failed for PaymentIntent " + paymentIntent.getId(), e);
        }
    }

    private void sendPurchasedEmail(User user, Cart cart, Transaction transaction) {
        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("user", user);
        templateModel.put("link", frontendDomain + "/login?redirect=/user/courses");
        templateModel.put("cart", cart);
        templateModel.put("transaction", transaction);

        emailService.sendHtmlEmail(
                user.getEmail(),
                "Payment Successful",
                "course-purchased",
                templateModel);
    }

}
