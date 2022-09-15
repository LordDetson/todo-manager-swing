package by.babanin.todo.application.exception;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class UncaughtExceptionHandlerDelegator implements UncaughtExceptionHandler {

    private final List<UncaughtExceptionHandler> uncaughtExceptionHandlers;

    public UncaughtExceptionHandlerDelegator(List<UncaughtExceptionHandler> uncaughtExceptionHandlers) {
        this.uncaughtExceptionHandlers = uncaughtExceptionHandlers;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        uncaughtExceptionHandlers.forEach(exceptionHandler -> exceptionHandler.uncaughtException(thread, throwable));
    }
}
