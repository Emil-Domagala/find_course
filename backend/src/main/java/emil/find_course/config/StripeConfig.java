package emil.find_course.config;

import org.springframework.context.annotation.Configuration;

import com.stripe.Stripe;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;

@Configuration
public class StripeConfig {

    Dotenv dotenv = Dotenv.load();

    private final String secretKey = dotenv.get("STRIPE_SECRET_KEY");

    @PostConstruct
    public void initStripe() {
        Stripe.apiKey = secretKey;
    }

}
