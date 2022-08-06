package by.babanin.todo.representation.exception;

public class RepresentationException extends RuntimeException {

    public RepresentationException(String message) {
        super(message);
    }

    public RepresentationException(String message, Throwable cause) {
        super(message, cause);
    }

    public RepresentationException(Throwable cause) {
        super(cause);
    }
}
