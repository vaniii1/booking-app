package vanii.bookingapp.service.payment;

import com.stripe.exception.StripeException;
import java.util.List;
import org.springframework.http.ResponseEntity;
import vanii.bookingapp.dto.payment.PaymentRequestDto;
import vanii.bookingapp.dto.payment.PaymentResponseDto;

public interface PaymentService {

    ResponseEntity<String> createPaymentAndReturnUrl(PaymentRequestDto request)
            throws StripeException;

    List<PaymentResponseDto> getPaymentsForCurrentUser();

    List<PaymentResponseDto> getPaymentsForCertainUser(Long userId);

    ResponseEntity<PaymentResponseDto> successPayment(String sessionId);

    ResponseEntity<String> cancelPayment(String sessionId);
}
