package by.babanin.todo.view.settings;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

@Component
public class SettingsObserverPostProcessor implements BeanPostProcessor {

    private final SettingsObserversDelegator delegator;

    public SettingsObserverPostProcessor(SettingsObserversDelegator delegator) {
        this.delegator = delegator;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if(bean instanceof SettingsObserver settingsObserver) {
            settingsObserver.register(delegator);
        }
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }
}
