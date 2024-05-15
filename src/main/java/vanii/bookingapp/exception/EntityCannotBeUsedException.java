package vanii.bookingapp.exception;

public class EntityCannotBeUsedException extends RuntimeException {
    public EntityCannotBeUsedException(String message) {
        super(message);
    }
}
