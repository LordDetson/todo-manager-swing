package by.babanin.todo.integration;

import org.springframework.context.ApplicationEvent;

import by.babanin.ext.settings.SettingsUpdateEvent;

public class SettingsUpdateEventAdapter extends ApplicationEvent {

    public SettingsUpdateEventAdapter(SettingsUpdateEvent event) {
        super(event);
    }

    @Override
    public SettingsUpdateEvent getSource() {
        return (SettingsUpdateEvent) super.getSource();
    }
}
