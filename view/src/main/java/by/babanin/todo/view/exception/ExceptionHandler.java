package by.babanin.todo.view.exception;

import java.lang.Thread.UncaughtExceptionHandler;

public class ExceptionHandler implements UncaughtExceptionHandler {

    private static final ExceptionHandler INSTANCE = new ExceptionHandler();

    public static ExceptionHandler getInstance() {
        return INSTANCE;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        handle(throwable);
    }

    public void handle(Throwable throwable) {
        throwable.printStackTrace();
        new ExceptionDialog(throwable).setVisible(true);
    }
}
