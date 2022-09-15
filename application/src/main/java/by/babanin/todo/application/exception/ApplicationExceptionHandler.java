package by.babanin.todo.application.exception;

import java.lang.Thread.UncaughtExceptionHandler;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import lombok.extern.log4j.Log4j2;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@Log4j2
public class ApplicationExceptionHandler implements UncaughtExceptionHandler {

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        log.error("Unexpected error occurred: " + throwable.getClass().getSimpleName(), throwable);
    }
}
