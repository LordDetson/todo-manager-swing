package by.babanin.todo.integration;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import by.babanin.ext.settings.SettingsSubscriber;
import by.babanin.ext.settings.SettingsUpdateEvent;

@Component
public class SettingsSubscriberDelegator implements SettingsSubscriber, ApplicationListener<SettingsUpdateEventAdapter> {

    private final List<SettingsSubscriber> subscribers = new ArrayList<>();
    private final ApplicationEventPublisher applicationEventPublisher;

    public SettingsSubscriberDelegator(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public void add(SettingsSubscriber subscriber) {
        subscribers.add(subscriber);
    }

    public void remove(SettingsSubscriber subscriber) {
        subscribers.remove(subscriber);
    }

    @Override
    public void handleSettingsUpdateEvent(SettingsUpdateEvent event) {
        applicationEventPublisher.publishEvent(new SettingsUpdateEventAdapter(event));
    }

    @Override
    public void onApplicationEvent(SettingsUpdateEventAdapter event) {
        subscribers.forEach(subscriber -> subscriber.handleSettingsUpdateEvent(event.getSource()));
    }
}
