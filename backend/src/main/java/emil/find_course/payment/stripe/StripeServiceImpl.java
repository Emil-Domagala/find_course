package emil.find_course.payment.stripe;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

import emil.find_course.cart.CartService;
import emil.find_course.cart.entity.Cart;
import emil.find_course.cart.entity.CartItem;
import emil.find_course.cart.repository.CartRepository;
import emil.find_course.common.service.EmailService;
import emil.find_course.course.courseStudent.CourseStudentService;
import emil.find_course.course.entity.Course;
import emil.find_course.course.enums.CourseStatus;
import emil.find_course.payment.stripe.dto.PaymentIntentResponse;
import emil.find_course.payment.stripe.exception.CustomStripeException;
import emil.find_course.payment.transaction.TransactionService;
import emil.find_course.payment.transaction.entity.Transaction;
import emil.find_course.user.UserService;
import emil.find_course.user.entity.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class StripeServiceImpl implements StripeService {

    @Value("${stripe.webhook.secret}")
    private String webhookSecret;

    @Value("${frontend.domain}")
    private String frontendDomain;

    private final EmailService emailService;
    private final UserService userService;
    private final CartService cartService;
    private final TransactionService transactionService;
    private final CartRepository cartRepository;
    private final CourseStudentService courseStudentService;

    @Override
    public PaymentIntentResponse createPaymentIntent(Cart cart, User user) {

        // Get valid courses
        PaymentIntentResponse response = new PaymentIntentResponse();

        int totalPrice = 0;
        Set<CartItem> validItems = new HashSet<>();
        Set<String> courseIds = new HashSet<>();
        boolean wasInvalid = false;

        for (CartItem item : cart.getCartItems()) {
            if (item.getCourse() != null && item.getCourse().getStatus().equals(CourseStatus.PUBLISHED)) {
                validItems.add(item);
                courseIds.add(item.getCourse().getId().toString());
                totalPrice += item.getPriceAtAddition();
            } else {
                wasInvalid = true;
            }
        }

        if (wasInvalid) {
            cart.getCartItems().clear();
            cart.getCartItems().addAll(validItems);
            cartRepository.save(cart);
            response.setWarnings(
                    List.of("Some courses were removed from your cart because they are no longer available."));
        }

        if (courseIds.size() == 0) {
            throw new IllegalArgumentException("Cart is empty.");
        }

        if (totalPrice <= 100) {
            throw new IllegalArgumentException("Cart total price must be positive.");
        }

        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setReceiptEmail(user.getEmail())
                .setAmount(((long) totalPrice))
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
            response.setClientSecret(intent.getClientSecret());
            return response;
        } catch (StripeException e) {
            log.error("Error creating PaymentIntent", e);
            throw new CustomStripeException("Error creating PaymentIntent");
        } catch (Exception e) {
            log.error("Error creating PaymentIntent", e);
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
                throw new CustomStripeException("Payment failed");

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
            Cart cart = cartService.findByUserWithItemsAndCourses(user);
            if (cart == null || cart.getCartItems().isEmpty()) {
                log.error("Webhook error: Cart not found or empty for user {} during fulfillment for PaymentIntent {}",
                        paymentIntent.getId());
                return;
            }

            Set<Course> courses = new HashSet<>();
            int totalPrice = 0;
            for (CartItem item : cart.getCartItems()) {
                if (item.getCourse() == null) {
                    continue;
                }
                courses.add(item.getCourse());
                totalPrice += item.getPriceAtAddition();
            }

            if (paymentIntent.getAmount() != (long) totalPrice) {
                log.error("Webhook error: Amount mismatch for PaymentIntent {}. Expected: {}, Actual: {}",
                        paymentIntent.getId(), totalPrice, paymentIntent.getAmount());
                return;
            }

            // !!!!!!!!!!!
            courseStudentService.grantAccessToCourse(user, courses);
            Transaction savedTransaction = transactionService.createTransaction(user, paymentIntent, cart);
            sendPurchasedEmail(user, cart, savedTransaction);
            log.info("deleting cart {}", cart.getId());
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
