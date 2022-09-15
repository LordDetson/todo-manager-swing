package by.babanin.todo.view.exception;

import java.lang.Thread.UncaughtExceptionHandler;

import org.springframework.stereotype.Component;

@Component
public class ViewExceptionHandler implements UncaughtExceptionHandler {

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        ExceptionDialog.display(throwable);
    }
}
