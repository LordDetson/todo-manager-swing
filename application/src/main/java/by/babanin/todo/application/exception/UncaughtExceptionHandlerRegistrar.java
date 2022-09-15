package by.babanin.todo.application.exception;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class UncaughtExceptionHandlerRegistrar implements ApplicationListener<ContextRefreshedEvent> {

    private final UncaughtExceptionHandlerDelegator uncaughtExceptionHandlerDelegator;

    public UncaughtExceptionHandlerRegistrar(UncaughtExceptionHandlerDelegator uncaughtExceptionHandlerDelegator) {
        this.uncaughtExceptionHandlerDelegator = uncaughtExceptionHandlerDelegator;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        Thread.setDefaultUncaughtExceptionHandler(uncaughtExceptionHandlerDelegator);
        Thread.currentThread().setUncaughtExceptionHandler(uncaughtExceptionHandlerDelegator);
    }
}
