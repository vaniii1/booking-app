package vanii.bookingapp.service.payment;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vanii.bookingapp.client.StripeClient;
import vanii.bookingapp.dto.payment.PaymentRequestDto;
import vanii.bookingapp.dto.payment.PaymentResponseDto;
import vanii.bookingapp.mapper.PaymentMapper;
import vanii.bookingapp.model.Accommodation;
import vanii.bookingapp.model.Booking;
import vanii.bookingapp.model.Payment;
import vanii.bookingapp.model.User;
import vanii.bookingapp.repository.booking.BookingRepository;
import vanii.bookingapp.repository.payment.PaymentRepository;
import vanii.bookingapp.service.booking.BookingService;
import vanii.bookingapp.service.user.UserService;

@Service
@Transactional
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final StripeClient stripeClient;
    private final UserService userService;
    private final PaymentRepository paymentRepository;
    private final BookingService bookingService;
    private final BookingRepository bookingRepository;
    private final PaymentMapper mapper;

    @Override
    public ResponseEntity<String> createPaymentAndReturnUrl(PaymentRequestDto request)
            throws StripeException {
        BigDecimal amount = countAmountForBooking(request.bookingId());
        Session session = stripeClient.createPaymentSession(amount);
        Payment payment = new Payment();
        Booking booking = new Booking().setId(request.bookingId());
        payment.setSessionId(session.getId())
                .setSessionUrl(session.getUrl())
                .setBooking(booking)
                .setUser(new User().setId(userService.getCurrentUser().getId()))
                .setAmount(amount);
        paymentRepository.save(payment);
        return ResponseEntity.status(HttpStatus.CREATED).body(payment.getSessionUrl());
    }

    @Override
    public ResponseEntity<PaymentResponseDto> successPayment(String sessionId) {
        Payment payment = getPaymentByIdOrThrowException(sessionId);
        payment.setStatus(Payment.Status.PAID);
        Booking booking = payment.getBooking();
        if (statusExpiredOrCanceled(booking.getStatus())) {
            Accommodation accommodation = booking.getAccommodation();
            accommodation.reduceByOne();
        }
        booking.setStatus(Booking.Status.CONFIRMED);
        payment.setBooking(booking);
        paymentRepository.save(payment);
        return ResponseEntity.ok(mapper.toDto(payment));
    }

    @Override
    public ResponseEntity<String> cancelPayment(String sessionId) {
        Payment payment = getPaymentByIdOrThrowException(sessionId);
        payment.setStatus(Payment.Status.CANCELED);
        Booking booking = payment.getBooking();
        if (!statusExpiredOrCanceled(booking.getStatus())) {
            Accommodation accommodation = booking.getAccommodation();
            accommodation.increaseByOne();
        }
        booking.setStatus(Booking.Status.CANCELED);
        payment.setBooking(booking);
        paymentRepository.save(payment);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Payment was Canceled. Please try again later.");
    }

    @Override
    public List<PaymentResponseDto> getPaymentsForCurrentUser() {
        return paymentRepository.getAllByUserId(userService.getCurrentUser().getId())
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public List<PaymentResponseDto> getPaymentsForCertainUser(Long userId) {
        return paymentRepository.getAllByUserId(userId)
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    private boolean statusExpiredOrCanceled(Booking.Status status) {
        return status == Booking.Status.EXPIRED || status == Booking.Status.CANCELED;
    }

    private BigDecimal countAmountForBooking(Long bookingId) {
        Booking booking = bookingService.getBookingOrThrowException(bookingId);
        long days = ChronoUnit.DAYS.between(booking.getCheckInDate(), booking.getCheckOutDate());
        Accommodation accommodation = booking.getAccommodation();
        return accommodation.getDailyRate().multiply(BigDecimal.valueOf(days));
    }

    private Payment getPaymentByIdOrThrowException(String sessionId) {
        return paymentRepository.findBySessionId(sessionId).orElseThrow(() ->
                new EntityNotFoundException("Can't find Payment with sessionId: " + sessionId));
    }
}
