package vanii.bookingapp.client;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class StripeClient {

    private static final String LOCALHOST_URL = "http://localhost";
    private static final String PAYMENT_PATH = "/booking-api/payments";
    private static final String SESSION_ID_PARAM = "session_id";
    private static final String SESSION_ID_VALUE = "{CHECKOUT_SESSION_ID}";
    private static final Long ONE_LONG = 1L;
    private static final String CURRENCY = "usd";
    private static final BigDecimal MULTIPLIER = new BigDecimal("100");
    private static final String PRODUCT_DATA_NAME = "Booking Payment";
    @Value("${server.port:defaultSecretKey}")
    private String serverPort;

    public Session createPaymentSession(BigDecimal amount) throws StripeException {
        long expiresAt = LocalDateTime.now().plusHours(22).toEpochSecond(ZoneOffset.UTC);
        String successUrl = UriComponentsBuilder.fromHttpUrl(LOCALHOST_URL)
                .port(serverPort)
                .path(PAYMENT_PATH + "/success")
                .queryParam(SESSION_ID_PARAM, SESSION_ID_VALUE)
                .toUriString();

        String cancelUrl = UriComponentsBuilder.fromHttpUrl(LOCALHOST_URL)
                .port(serverPort)
                .path(PAYMENT_PATH + "/cancel")
                .queryParam(SESSION_ID_PARAM, SESSION_ID_VALUE)
                .toUriString();

        SessionCreateParams params = SessionCreateParams.builder()
                 .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                 .setMode(SessionCreateParams.Mode.PAYMENT)
                 .setSuccessUrl(successUrl)
                 .setCancelUrl(cancelUrl)
                 .setExpiresAt(expiresAt)
                 .addLineItem(SessionCreateParams.LineItem.builder()
                        .setQuantity(ONE_LONG)
                        .setPriceData(SessionCreateParams
                                 .LineItem.PriceData.builder()
                                 .setCurrency(CURRENCY)
                                 .setUnitAmount(amount.multiply(MULTIPLIER).longValue())
                                 .setProductData(SessionCreateParams.LineItem.PriceData.ProductData
                                         .builder()
                                         .setName(PRODUCT_DATA_NAME)
                                         .build())
                                 .build())
                        .build())
                 .build();
        return com.stripe.model.checkout.Session.create(params);
    }
}
