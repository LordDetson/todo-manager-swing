package by.babanin.todo.view.settings;

import by.babanin.todo.view.config.SettingsUpdateEvent;

public interface SettingsObserver {

    void register(SettingsObserversDelegator delegator);

    void handleSettingsUpdateEvent(SettingsUpdateEvent event);
}
