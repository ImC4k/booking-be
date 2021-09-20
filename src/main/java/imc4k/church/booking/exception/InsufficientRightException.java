package imc4k.church.booking.exception;

public class InsufficientRightException extends RuntimeException {
    public InsufficientRightException() {
        super("Insufficient access right");
    }
}
