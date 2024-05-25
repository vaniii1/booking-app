package vanii.bookingapp.exception;

public class CanceledPaymentException extends RuntimeException {
    public CanceledPaymentException(String message) {
        super(message);
    }
}
