package by.babanin.todo.view.component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class LazyContainer<T> {

    private final List<LazyContainerListener<T>> listeners = new ArrayList<>();

    private final Supplier<T> factory;
    private T component;
    private boolean initialized;

    public LazyContainer(Supplier<T> factory) {
        this.factory = factory;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public T get() {
        if(!initialized) {
            listeners.forEach(LazyContainerListener::beforeInitialization);
            component = factory.get();
            initialized = true;
            listeners.forEach(lazyContainerListener -> lazyContainerListener.afterInitialization(component));
        }
        return component;
    }

    public LazyContainer<T> addListener(LazyContainerListener<T> listener) {
        listeners.add(listener);
        return this;
    }
}
