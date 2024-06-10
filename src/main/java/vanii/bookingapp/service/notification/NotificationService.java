package vanii.bookingapp.service.notification;

import java.util.List;
import vanii.bookingapp.model.Accommodation;
import vanii.bookingapp.model.Booking;
import vanii.bookingapp.model.Payment;

public interface NotificationService {
    void notifyNewBooking(Booking booking);

    void notifyBookingCancellation(Booking booking);

    void notifyExpiredBookings(List<Booking> list);

    void notifyNoExpiredBookingsToday();

    void notifyNewAccommodation(Accommodation accommodation);

    void notifyAccommodationRelease(Accommodation accommodation);

    void notifySuccessfulPayment(Payment payment);

    void notifyCanceledPayment(Payment payment);

    void notifyExpiredPayment(Payment payment);
}
