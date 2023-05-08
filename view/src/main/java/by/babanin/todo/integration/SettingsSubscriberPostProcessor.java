package by.babanin.todo.integration;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import by.babanin.ext.settings.SettingsSubscriber;

@Component
public class SettingsSubscriberPostProcessor implements BeanPostProcessor {

    private final SettingsSubscriberDelegator delegator;

    public SettingsSubscriberPostProcessor(SettingsSubscriberDelegator delegator) {
        this.delegator = delegator;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if(bean instanceof SettingsSubscriber subscriber) {
            delegator.add(subscriber);
        }
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }
}
