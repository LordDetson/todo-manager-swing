package by.babanin.todo.view;

public interface Loadable {

    void load();

    void clear();

    default void reload() {
        clear();
        load();
    }
}
