package by.babanin.todo.view;

import java.awt.EventQueue;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Component;

import by.babanin.todo.view.translat.Translator;

@Component
public class UILauncher implements ApplicationListener<ContextRefreshedEvent> {

    private final ResourceBundleMessageSource messageSource;

    public UILauncher(ResourceBundleMessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        Translator.setMessageSource(messageSource);
        EventQueue.invokeLater(() -> {
            TodoFrame todoFrame = new TodoFrame();
            todoFrame.setVisible(true);
        });
    }
}
