package vanii.bookingapp.client;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class StripeClient {
    private static final String SUCCESS_URL = "http://localhost:%s/booking-api/"
            + "payments/success?session_id={CHECKOUT_SESSION_ID}";
    private static final String CANCEL_URL = "http://localhost:%s/booking-api/"
            + "payments/cancel?session_id={CHECKOUT_SESSION_ID}";
    private static final Long ONE_LONG = 1L;
    private static final String CURRENCY = "usd";
    private static final BigDecimal MULTIPLIER = new BigDecimal("100");
    private static final String PRODUCT_DATA_NAME = "Booking Payment";
    @Value("${server.port:8080}")
    private String serverPort;

    public Session createPaymentSession(BigDecimal amount) throws StripeException {
        SessionCreateParams params = SessionCreateParams.builder()
                 .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                 .setMode(SessionCreateParams.Mode.PAYMENT)
                 .setSuccessUrl(String.format(SUCCESS_URL, serverPort))
                 .setCancelUrl(String.format(CANCEL_URL, serverPort))
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
