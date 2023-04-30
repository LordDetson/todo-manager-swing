package by.babanin.todo.view.settings;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class SettingsObserversDelegator implements ApplicationListener<SettingsUpdateEvent> {

    private final List<SettingsObserver> observers = new ArrayList<>();

    public void add(SettingsObserver observer) {
        observers.add(observer);
    }

    public void remove(SettingsObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void onApplicationEvent(SettingsUpdateEvent event) {
        observers.forEach(observer -> observer.handleSettingsUpdateEvent(event));
    }
}
