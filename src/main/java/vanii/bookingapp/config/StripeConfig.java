package vanii.bookingapp.config;

import com.stripe.Stripe;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:application.properties")
public class StripeConfig {
    @Value("${stripe.ApiKey:sk_test_51PGgiVAmFYrK3gvz2MB6QFSDy8j5I60f"
            + "ZEKG6UiV4zfcWdUx9NKXRykb6Oi2HcZfF6HWfePBUcIgIht0MftjEbHy00JiZPbudM}")
    private String apiKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = apiKey;
    }
}
