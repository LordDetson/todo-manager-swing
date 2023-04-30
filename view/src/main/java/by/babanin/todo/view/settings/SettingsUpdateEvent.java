package by.babanin.todo.view.settings;

import org.springframework.context.ApplicationEvent;

public class SettingsUpdateEvent extends ApplicationEvent {

    public SettingsUpdateEvent(Setting setting) {
        super(setting);
    }

    @Override
    public Setting getSource() {
        return (Setting) super.getSource();
    }
}
