package by.babanin.todo.integration;

import java.awt.EventQueue;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import by.babanin.todo.ui.MainFrame;

@Component
public class UILauncher implements ApplicationListener<ContextRefreshedEvent> {

    private final MainFrame mainFrame;

    public UILauncher(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        EventQueue.invokeLater(this::showMainFrame);
    }

    private void showMainFrame() {
        mainFrame.setVisible(true);
        mainFrame.load();
    }
}
