package by.babanin.todo.integration;

import java.lang.Thread.UncaughtExceptionHandler;

import javax.swing.Icon;

import org.springframework.stereotype.Component;

import by.babanin.ext.component.ExceptionDialog;

@Component
public final class ViewExceptionHandler implements UncaughtExceptionHandler {

    private final Icon errorIcon;

    public ViewExceptionHandler(Icon errorIcon) {
        this.errorIcon = errorIcon;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        ExceptionDialog.display(throwable, errorIcon);
    }
}
