package by.babanin.todo.integration;

import java.awt.EventQueue;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import by.babanin.ext.settings.Settings;
import by.babanin.ext.settings.SettingsUpdateEvent;
import by.babanin.ext.settings.style.StyleSetting;
import by.babanin.todo.ui.MainFrame;

@Component
public class UILauncher implements ApplicationListener<ContextRefreshedEvent> {

    private final Settings settings;
    private final StyleHandler styleHandler;
    private final MainFrame mainFrame;

    public UILauncher(Settings settings, StyleHandler styleHandler, MainFrame mainFrame) {
        this.settings = settings;
        this.styleHandler = styleHandler;
        this.mainFrame = mainFrame;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        SettingsUpdateEvent updateEvent = new SettingsUpdateEvent(settings.get(StyleSetting.ID));
        styleHandler.handleSettingsUpdateEvent(updateEvent);
        EventQueue.invokeLater(this::showMainFrame);
    }

    private void showMainFrame() {
        mainFrame.setVisible(true);
        mainFrame.load();
    }
}
