package exception;

public class NotYourMoveException extends RuntimeException {
    public NotYourMoveException(String message) {
        super(message);
    }
}
