package by.babanin.todo.view.config;

import org.springframework.context.ApplicationEvent;

import by.babanin.todo.view.settings.Setting;

public class SettingsUpdateEvent extends ApplicationEvent {

    public SettingsUpdateEvent(Setting setting) {
        super(setting);
    }

    @Override
    public Setting getSource() {
        return (Setting) super.getSource();
    }
}
