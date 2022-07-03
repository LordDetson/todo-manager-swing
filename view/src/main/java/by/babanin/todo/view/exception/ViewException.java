package by.babanin.todo.view.exception;

public class ViewException extends RuntimeException {

    public ViewException(String message) {
        super(message);
    }

    public ViewException(String message, Throwable cause) {
        super(message, cause);
    }
}
