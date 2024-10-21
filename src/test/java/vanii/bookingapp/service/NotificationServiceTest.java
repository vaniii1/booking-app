package vanii.bookingapp.service;

import static org.mockito.Mockito.verify;

import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import vanii.bookingapp.client.MyTelegramBot;
import vanii.bookingapp.model.Accommodation;
import vanii.bookingapp.model.Booking;
import vanii.bookingapp.model.Payment;
import vanii.bookingapp.service.notification.NotificationServiceImpl;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {
    private static final long ID = 1L;

    @Value("{admin.chat.id}")
    private static String ADMIN_ID;
    private static Booking booking;
    private static Accommodation accommodation;
    private static Payment payment;
    @Mock
    private MyTelegramBot myTelegramBot;
    @InjectMocks
    private NotificationServiceImpl notificationService;

    @Test
    @DisplayName("""
            Verify notifyNewBooking() method works     
            """)
    void notifyNewBooking_ValidRequest_ValidResponse() {
        notificationService.notifyNewBooking(booking);
        String expectedMessage = "New Booking was created.\n Booking: "
                + booking.toString();
        verify(myTelegramBot).sendNotification(ADMIN_ID, expectedMessage);
    }

    @Test
    @DisplayName("""
            Verify notifyBookingCancellation() method works
            """)
    void notifyBookingCancellation_ValidRequest_ValidResponse() {
        notificationService.notifyBookingCancellation(booking);
        String expectedMessage = "Booking was canceled.\n Booking: "
                + booking.toString();
        verify(myTelegramBot).sendNotification(ADMIN_ID, expectedMessage);
    }

    @Test
    @DisplayName("""
            Verify notifyExpiredBookings() method works
            """)
    void notifyExpiredBookings_ValidRequest_ValidResponse() {
        notificationService.notifyExpiredBookings(List.of(booking));
        String expectedMessage = "Bookings were expired today.\n Bookings: "
                + List.of(booking);
        verify(myTelegramBot).sendNotification(ADMIN_ID, expectedMessage);
    }

    @Test
    @DisplayName("""
            Verify notifyNoExpiredBookingsToday() method works
            """)
    void notifyNoExpiredBookingsToday_ValidRequest_ValidResponse() {
        notificationService.notifyNoExpiredBookingsToday();
        String expectedMessage = "No expired bookings today!";
        verify(myTelegramBot).sendNotification(ADMIN_ID, expectedMessage);
    }

    @Test
    @DisplayName("""
            Verify notifyNewAccommodation() method works
            """)
    void notifyNewAccommodation_ValidRequest_ValidResponse() {
        notificationService.notifyNewAccommodation(accommodation);
        String expectedMessage = "New Accommodation was created.\n Accommodation: "
                + accommodation.toString();;
        verify(myTelegramBot).sendNotification(ADMIN_ID, expectedMessage);
    }

    @Test
    @DisplayName("""
            Verify notifyAccommodationRelease() method works
            """)
    void notifyAccommodationRelease_ValidRequest_ValidResponse() {
        notificationService.notifyAccommodationRelease(accommodation);
        String expectedMessage = "Accommodation was released.\n Accommodation: "
                + accommodation.toString();
        verify(myTelegramBot).sendNotification(ADMIN_ID, expectedMessage);
    }

    @Test
    @DisplayName("""
            Verify notifySuccessfulPayment() method works
            """)
    void notifySuccessfulPayment_ValidRequest_ValidResponse() {
        notificationService.notifySuccessfulPayment(payment);
        String expectedMessage = "Payment was successful.\n Payment: "
                + payment.toString();
        verify(myTelegramBot).sendNotification(ADMIN_ID, expectedMessage);
    }

    @Test
    @DisplayName("""
            Verify notifyCanceledPayment() method works
            """)
    void notifyCanceledPayment_ValidRequest_ValidResponse() {
        notificationService.notifyCanceledPayment(payment);
        String expectedMessage = "Payment was canceled.\n Payment: " + payment.toString();
        verify(myTelegramBot).sendNotification(ADMIN_ID, expectedMessage);
    }

    @Test
    @DisplayName("""
            Verify notifyExpiredPayment() method works
            """)
    void notifyExpiredPayment_ValidRequest_ValidResponse() {
        notificationService.notifyExpiredPayment(payment);
        String expectedMessage = "Payment was expired.\n Payment: " + payment.toString();
        verify(myTelegramBot).sendNotification(ADMIN_ID, expectedMessage);
    }

    @BeforeAll
    static void setUp() {
        booking = new Booking().setId(ID);
        accommodation = new Accommodation().setId(ID);
        payment = new Payment().setId(ID);
    }
}
