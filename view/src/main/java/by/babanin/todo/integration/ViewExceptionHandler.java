package by.babanin.todo.integration;

import java.lang.Thread.UncaughtExceptionHandler;

import org.springframework.stereotype.Component;

import by.babanin.ext.component.ExceptionDialog;
import by.babanin.todo.image.IconResources;

@Component
public final class ViewExceptionHandler implements UncaughtExceptionHandler {

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        ExceptionDialog.display(throwable, IconResources.getIcon("error", 48));
    }
}
