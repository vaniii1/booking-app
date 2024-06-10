package vanii.bookingapp.controller;

import com.stripe.exception.StripeException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vanii.bookingapp.dto.payment.PaymentRequestDto;
import vanii.bookingapp.dto.payment.PaymentResponseDto;
import vanii.bookingapp.service.payment.PaymentService;

@Tag(name = "Payment Management",
        description = "Endpoints indicate specific actions with Payments")
@RequiredArgsConstructor
@RestController
@RequestMapping("/payments")
public class PaymentController {
    private final PaymentService paymentService;

    @Operation(summary = "Create new Payment",
            description = "Create a new Payment with certain amount and bookingId")
    @PostMapping
    public ResponseEntity<PaymentResponseDto> createBookingPayment(
            @RequestBody @Valid PaymentRequestDto request
    ) throws StripeException {
        return paymentService.createPayment(request);
    }

    @Operation(summary = "Successful response",
            description = "Get successful response if params in payment was correct")
    @PutMapping("/success")
    public ResponseEntity<PaymentResponseDto> successPayment(
            @RequestParam("session_id") String sessionId
    ) {
        return paymentService.successPayment(sessionId);
    }

    @Operation(summary = "Bad Request",
            description = "Get canceled payment response if params in payment were not correct")
    @PutMapping("/cancel")
    public ResponseEntity<String> cancelPayment(
            @RequestParam("session_id") String sessionId
    ) {
        return paymentService.cancelPayment(sessionId);
    }

    @Operation(summary = "Renew Payment",
            description = "Generates new Payment by SessionId if previous was Expired")
    @PostMapping("/renew")
    public ResponseEntity<PaymentResponseDto> renewPayment(@RequestParam String sessionId)
            throws StripeException {
        return paymentService.renewPaymentSession(sessionId);
    }

    @Operation(summary = "Get Payments for current User",
            description = "Get all payment for currently logged in User")
    @GetMapping("/my")
    public List<PaymentResponseDto> getPaymentsForCurrentUser() {
        return paymentService.getPaymentsForCurrentUser();
    }

    @Operation(summary = "Get Payments for certain User",
            description = "Get all payment for certain User by Id")
    @PreAuthorize("hasAuthority('MANAGER')")
    @GetMapping
    public List<PaymentResponseDto> getPaymentsForCertainUser(
            @RequestParam("user_id") Long userId) {
        return paymentService.getPaymentsForCertainUser(userId);
    }
}
