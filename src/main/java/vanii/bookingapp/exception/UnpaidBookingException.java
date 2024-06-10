package vanii.bookingapp.exception;

public class UnpaidBookingException extends RuntimeException {
    public UnpaidBookingException(String message) {
        super(message);
    }
}
