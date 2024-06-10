package vanii.bookingapp.service.notification;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import vanii.bookingapp.client.MyTelegramBot;
import vanii.bookingapp.model.Accommodation;
import vanii.bookingapp.model.Booking;
import vanii.bookingapp.model.Payment;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final MyTelegramBot myTelegramBot;
    @Value("${admin.chat.id}")
    private String chatId;

    @Override
    public void notifyNewBooking(Booking booking) {
        String message = "New Booking was created.\n Booking: "
                + booking.toString();
        myTelegramBot.sendNotification(chatId, message);
    }

    @Override
    public void notifyBookingCancellation(Booking booking) {
        String message = "Booking was canceled.\n Booking: "
                + booking.toString();
        myTelegramBot.sendNotification(chatId, message);
    }

    @Override
    public void notifyExpiredBookings(List<Booking> list) {
        String message = "Bookings were expired today.\n Bookings: "
                + list.toString();
        myTelegramBot.sendNotification(chatId, message);
    }

    @Override
    public void notifyNoExpiredBookingsToday() {
        String message = "No expired bookings today!";
        myTelegramBot.sendNotification(chatId, message);
    }

    @Override
    public void notifyNewAccommodation(Accommodation accommodation) {
        String message = "New Accommodation was created.\n Accommodation: "
                + accommodation.toString();
        myTelegramBot.sendNotification(chatId, message);
    }

    @Override
    public void notifyAccommodationRelease(Accommodation accommodation) {
        String message = "Accommodation was released.\n Accommodation: "
                + accommodation.toString();
        myTelegramBot.sendNotification(chatId, message);
    }

    @Override
    public void notifySuccessfulPayment(Payment payment) {
        String message = "Payment was successful.\n Payment: "
                + payment.toString();
        myTelegramBot.sendNotification(chatId, message);
    }

    @Override
    public void notifyCanceledPayment(Payment payment) {
        String message = "Payment was canceled.\n Payment: " + payment.toString();
        myTelegramBot.sendNotification(chatId, message);
    }

    @Override
    public void notifyExpiredPayment(Payment payment) {
        String message = "Payment was expired.\n Payment: " + payment.toString();
        myTelegramBot.sendNotification(chatId, message);
    }
}
