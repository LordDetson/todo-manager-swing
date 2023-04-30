package by.babanin.todo.view.settings;

public interface SettingsObserver {

    void register(SettingsObserversDelegator delegator);

    void handleSettingsUpdateEvent(SettingsUpdateEvent event);
}
