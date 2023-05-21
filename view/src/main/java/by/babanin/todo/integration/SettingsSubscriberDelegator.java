package by.babanin.todo.integration;

import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import by.babanin.ext.settings.SettingsSubscriber;
import by.babanin.ext.settings.SettingsUpdateEvent;

@Component
public class SettingsSubscriberDelegator implements SettingsSubscriber, ApplicationListener<SettingsUpdateEventAdapter> {

    private final ApplicationEventPublisher applicationEventPublisher;
    private final List<SettingsSubscriber> subscribers;

    public SettingsSubscriberDelegator(ApplicationEventPublisher applicationEventPublisher, List<SettingsSubscriber> subscribers) {
        this.applicationEventPublisher = applicationEventPublisher;
        this.subscribers = subscribers;
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
