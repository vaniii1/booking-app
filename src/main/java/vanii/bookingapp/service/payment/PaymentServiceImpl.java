package vanii.bookingapp.service.payment;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.hibernate.SessionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
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
import vanii.bookingapp.repository.payment.PaymentRepository;
import vanii.bookingapp.service.booking.BookingService;
import vanii.bookingapp.service.notification.NotificationService;
import vanii.bookingapp.service.user.UserService;

@Service
@Transactional
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final NotificationService notificationService;
    private final StripeClient stripeClient;
    private final UserService userService;
    private final PaymentRepository paymentRepository;
    private final BookingService bookingService;
    private final PaymentMapper mapper;

    @Override
    public ResponseEntity<PaymentResponseDto> createPayment(PaymentRequestDto request)
            throws StripeException {
        BigDecimal amount = calculateAmountForBooking(request.bookingId());
        Session session = stripeClient.createPaymentSession(amount);
        Payment payment = new Payment();
        Booking booking = new Booking().setId(request.bookingId());
        payment.setSessionId(session.getId())
                .setSessionUrl(session.getUrl())
                .setBooking(booking)
                .setUser(new User().setId(userService.getCurrentUser().getId()))
                .setAmount(amount);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapper.toDto(paymentRepository.save(payment)));
    }

    @Override
    public ResponseEntity<PaymentResponseDto> successPayment(String sessionId) {
        Payment payment = getPaymentByIdOrThrowException(sessionId);
        payment.setStatus(Payment.Status.PAID);
        Booking booking = payment.getBooking();
        adjustAccommodationAvailabilityIfCanceled(booking);
        booking.setStatus(Booking.Status.CONFIRMED);
        payment.setBooking(booking);
        paymentRepository.save(payment);
        notificationService.notifySuccessfulPayment(payment);
        return ResponseEntity.ok(mapper.toDto(payment));
    }

    @Override
    public ResponseEntity<String> cancelPayment(String sessionId) {
        Payment payment = getPaymentByIdOrThrowException(sessionId);
        payment.setStatus(Payment.Status.CANCELED);
        Booking booking = payment.getBooking();
        adjustAccommodationAvailabilityIfNotCanceled(booking);
        booking.setStatus(Booking.Status.CANCELED);
        payment.setBooking(booking);
        paymentRepository.save(payment);
        notificationService.notifyCanceledPayment(payment);
        notificationService.notifyBookingCancellation(booking);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Payment was Canceled. Please try again later.");
    }

    @Override
    public List<PaymentResponseDto> getPaymentsForCurrentUser() {
        return getAllPaymentsByUserId(userService.getCurrentUser().getId());
    }

    @Override
    public List<PaymentResponseDto> getPaymentsForCertainUser(Long userId) {
        return getAllPaymentsByUserId(userId);
    }

    @Override
    public ResponseEntity<PaymentResponseDto> renewPaymentSession(String sessionId)
            throws StripeException {
        Payment payment = getPaymentByIdOrThrowException(sessionId);
        if (payment.getStatus() != Payment.Status.EXPIRED) {
            throw new SessionException("Session is not expired. Payment Status: "
                    + payment.getStatus());
        }
        BigDecimal amount = payment.getAmount();
        Session session = stripeClient.createPaymentSession(amount);
        payment.setSessionId(session.getId())
                .setSessionUrl(session.getUrl())
                .setStatus(Payment.Status.PENDING);
        Payment savedPayment = paymentRepository.save(payment);
        return ResponseEntity.ok(mapper.toDto(savedPayment));
    }

    @Scheduled(fixedRate = 60000)
    public void checkExpiredSessions() {
        List<Payment> activePayments = paymentRepository.findAllByStatus(Payment.Status.PENDING);
        for (Payment payment : activePayments) {
            try {
                Session session = Session.retrieve(payment.getSessionId());
                if (session.getExpiresAt() < Instant.now().getEpochSecond()) {
                    payment.setStatus(Payment.Status.EXPIRED);
                    paymentRepository.save(payment);
                    notificationService.notifyExpiredPayment(payment);
                }
            } catch (StripeException e) {
                System.out.println("Couldn't retrieve session from sessionId. SessionId: "
                        + payment.getSessionId());
            }
        }
    }

    private void adjustAccommodationAvailabilityIfCanceled(Booking booking) {
        if (statusExpiredOrCanceled(booking.getStatus())) {
            Accommodation accommodation = booking.getAccommodation();
            accommodation.adjustAvailability(-1);
        }
    }

    private void adjustAccommodationAvailabilityIfNotCanceled(Booking booking) {
        if (!statusExpiredOrCanceled(booking.getStatus())) {
            Accommodation accommodation = booking.getAccommodation();
            accommodation.adjustAvailability(1);
            notificationService.notifyAccommodationRelease(booking.getAccommodation());
        }
    }

    private boolean statusExpiredOrCanceled(Booking.Status status) {
        return status == Booking.Status.EXPIRED || status == Booking.Status.CANCELED;
    }

    private BigDecimal calculateAmountForBooking(Long bookingId) {
        Booking booking = bookingService.getBookingOrThrowException(bookingId);
        long days = ChronoUnit.DAYS.between(booking.getCheckInDate(), booking.getCheckOutDate());
        return booking.getAccommodation().getDailyRate().multiply(BigDecimal.valueOf(days));
    }

    private Payment getPaymentByIdOrThrowException(String sessionId) {
        return paymentRepository.findBySessionId(sessionId).orElseThrow(() ->
                new EntityNotFoundException("Can't find Payment with sessionId: " + sessionId));
    }

    private List<PaymentResponseDto> getAllPaymentsByUserId(Long userId) {
        return paymentRepository.getAllByUserId(userId)
                .stream()
                .map(mapper::toDto)
                .toList();
    }
}
