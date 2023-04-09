package by.babanin.todo.view;

import java.awt.EventQueue;

import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Component;

import by.babanin.todo.view.translat.Translator;

@Component
public abstract class UILauncher implements ApplicationListener<ContextRefreshedEvent> {

    private final ResourceBundleMessageSource messageSource;

    protected UILauncher(ResourceBundleMessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        Translator.setMessageSource(messageSource);
        EventQueue.invokeLater(this::showMainFrame);
    }

    private void showMainFrame() {
        createMainFrame().setVisible(true);
    }

    @Lookup
    protected abstract MainFrame createMainFrame();
}
