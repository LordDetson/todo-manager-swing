package by.babanin.todo.view.component;

@FunctionalInterface
public interface LazyContainerListener<T> {

    default void beforeInitialization() {
        // do nothing by default
    }

    void afterInitialization(T component);
}
