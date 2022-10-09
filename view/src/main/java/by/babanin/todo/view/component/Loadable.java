package by.babanin.todo.view.component;

public interface Loadable {

    void load();

    void clear();

    default void reload() {
        clear();
        load();
    }
}
