package vanii.bookingapp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import vanii.bookingapp.client.StripeClient;
import vanii.bookingapp.dto.payment.PaymentRequestDto;
import vanii.bookingapp.dto.payment.PaymentResponseDto;
import vanii.bookingapp.mapper.PaymentMapper;
import vanii.bookingapp.model.Accommodation;
import vanii.bookingapp.model.Booking;
import vanii.bookingapp.model.Payment;
import vanii.bookingapp.model.User;
import vanii.bookingapp.repository.payment.PaymentRepository;
import vanii.bookingapp.service.booking.BookingServiceImpl;
import vanii.bookingapp.service.notification.NotificationService;
import vanii.bookingapp.service.payment.PaymentServiceImpl;
import vanii.bookingapp.service.user.UserServiceImpl;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {
    private static PaymentRequestDto requestDto;
    private static User user;
    private static Booking booking;
    private static Accommodation accommodation;
    private static Session session;
    private static Payment payment;
    private static PaymentResponseDto expected;
    private static final Long LONG_ONE = 1L;
    private static final int INT_FIVE = 5;
    @Mock
    private NotificationService notificationService;
    @Mock
    private StripeClient stripeClient;
    @Mock
    private UserServiceImpl userService;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private BookingServiceImpl bookingService;
    @Mock
    private PaymentMapper mapper;
    @InjectMocks
    private PaymentServiceImpl paymentService;

    @Test
    @DisplayName("""
            Verify createPayment() method works
            """)
    void createPayment_ValidRequest_CorrectResponse() throws StripeException {
        when(bookingService.getBookingOrThrowException(LONG_ONE)).thenReturn(booking);
        when(stripeClient.createPaymentSession(BigDecimal.valueOf(INT_FIVE)))
                .thenReturn(session);
        when(userService.getCurrentUser()).thenReturn(user);
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);
        when(mapper.toDto(payment)).thenReturn(expected);

        PaymentResponseDto actual = paymentService.createPayment(requestDto).getBody();

        assertEquals(expected, actual);
        verifyNoMoreInteractions(bookingService, stripeClient,
                userService, paymentRepository, mapper);
    }

    @Test
    @DisplayName("""
            Verify getPaymentsForCurrentUser() method works
            """)
    void getPaymentsForCurrentUser_ValidRequest_CorrectResponse() {
        List<Payment> expectedList = List.of(payment);
        when(userService.getCurrentUser()).thenReturn(user);
        when(paymentRepository.getAllByUserId(any())).thenReturn(expectedList);
        when(mapper.toDto(payment)).thenReturn(expected);

        List<PaymentResponseDto> actualList = paymentService.getPaymentsForCurrentUser();

        assertEquals(expectedList.size(), actualList.size());
        assertEquals(expected, actualList.get(0));

        verifyNoMoreInteractions(userService, paymentRepository, mapper);
    }

    @Test
    @DisplayName("""
            Verify getPaymentsForCertainUser() method works 
            """)
    void getPaymentsForCertainUser_ValidRequest_CorrectResponse() {
        List<Payment> expectedList = List.of(payment);
        when(paymentRepository.getAllByUserId(any())).thenReturn(expectedList);
        when(mapper.toDto(payment)).thenReturn(expected);

        List<PaymentResponseDto> actualList = paymentService
                .getPaymentsForCertainUser(any());

        assertEquals(expectedList.size(), actualList.size());
        assertEquals(expected, actualList.get(0));

        verifyNoMoreInteractions(paymentRepository, mapper);
    }

    @Test
    @DisplayName("""
            Verify successPayment() method works
            """)
    void successPayment_ValidRequest_CorrectResponse() {
        when(paymentRepository.findBySessionId(any())).thenReturn(Optional.of(payment));
        when(paymentRepository.save(payment)).thenReturn(payment);
        doNothing().when(notificationService).notifySuccessfulPayment(payment);
        when(mapper.toDto(payment)).thenReturn(expected);

        PaymentResponseDto actual = paymentService.successPayment(any()).getBody();

        assertEquals(expected, actual);
        verifyNoMoreInteractions(paymentRepository, notificationService, mapper);
    }

    @Test
    @DisplayName("""
            Verify cancelPayment() method works 
            """)
    void cancelPayment_ValidRequest_CorrectResponse() {
        when(paymentRepository.findBySessionId(any())).thenReturn(Optional.of(payment));
        doNothing().when(notificationService).notifyAccommodationRelease(accommodation);
        when(paymentRepository.save(payment)).thenReturn(payment);
        doNothing().when(notificationService).notifyCanceledPayment(payment);
        doNothing().when(notificationService).notifyBookingCancellation(booking);

        String expectedString = "Payment was Canceled. Please try again later.";
        String actualString = paymentService.cancelPayment(any()).getBody();

        assertEquals(expectedString, actualString);
        verifyNoMoreInteractions(paymentRepository, notificationService);
    }

    @Test
    @DisplayName("""
            Verify renewPaymentSession() method works
            """)
    void renewPaymentSession_ValidRequest_CorrectResponse() throws StripeException {
        payment.setStatus(Payment.Status.EXPIRED);
        when(paymentRepository.findBySessionId(any()))
                .thenReturn(Optional.of(payment));
        when(stripeClient.createPaymentSession(any())).thenReturn(session);
        when(paymentRepository.save(payment)).thenReturn(payment);
        when(mapper.toDto(payment)).thenReturn(expected);

        PaymentResponseDto actual = paymentService.renewPaymentSession(any()).getBody();

        assertEquals(expected, actual);
        verifyNoMoreInteractions(paymentRepository, stripeClient, mapper);
    }

    @Test
    @DisplayName("""
            Verify renewPaymentSession() method throws exception 
            """)
    void renewPaymentSession_InvalidRequest_ThrowsException() {
        when(paymentRepository.findBySessionId(any())).thenReturn(Optional.of(payment));

        Exception exception = assertThrows(
                RuntimeException.class,
                () -> paymentService.renewPaymentSession(any())
        );

        String expectedMessage = "Session is not expired. Payment Status: " + payment.getStatus();
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
        verifyNoMoreInteractions(paymentRepository);
    }

    @Test
    void checkExpiredSessions_ValidRequest_CorrectResponse() {
        Session session = mock(Session.class);
        when(session.getExpiresAt()).thenReturn(Instant.now().getEpochSecond() - 1);
        when(paymentRepository.findAllByStatus(Payment.Status.PENDING))
                .thenReturn(List.of(payment));

        try (MockedStatic<Session> mockedSession = mockStatic(Session.class)) {
            mockedSession.when(() -> Session.retrieve(anyString())).thenReturn(session);

            when(paymentRepository.save(payment)).thenReturn(payment);
            doNothing().when(notificationService).notifyExpiredPayment(payment);

            paymentService.checkExpiredSessions();

            verifyNoMoreInteractions(paymentRepository, notificationService);
        }
    }

    @BeforeAll
    static void setUps() {
        requestDto = new PaymentRequestDto(LONG_ONE);
        user = new User().setId(LONG_ONE);
        accommodation = new Accommodation()
                .setId(LONG_ONE)
                .setDailyRate(BigDecimal.valueOf(INT_FIVE))
                .setAvailability(2);
        booking = new Booking()
                .setId(LONG_ONE)
                .setUser(user)
                .setStatus(Booking.Status.PENDING)
                .setAccommodation(accommodation)
                .setCheckInDate(LocalDate.now())
                .setCheckOutDate(LocalDate.now().plusDays(LONG_ONE));
        session = new Session();
        session.setId("id");
        session.setUrl("url");
        session.setExpiresAt(LocalDateTime.now().plusHours(22).toEpochSecond(ZoneOffset.UTC));
        payment = new Payment()
                .setId(LONG_ONE)
                .setAmount(accommodation.getDailyRate())
                .setSessionId(session.getId())
                .setSessionUrl(session.getUrl())
                .setBooking(booking)
                .setUser(user);
        expected = new PaymentResponseDto()
                .setId(payment.getId())
                .setAmount(payment.getAmount())
                .setSessionId(payment.getSessionId())
                .setSessionUrl(payment.getSessionUrl())
                .setStatus(payment.getStatus())
                .setBookingId(LONG_ONE)
                .setUserId(LONG_ONE);
    }

    @AfterEach
    void tearDown() {
        payment.setStatus(Payment.Status.PENDING);
        booking.setStatus(Booking.Status.PENDING);
        accommodation.setAvailability(2);
    }
}
