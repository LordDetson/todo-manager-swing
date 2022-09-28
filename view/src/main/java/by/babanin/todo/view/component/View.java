package by.babanin.todo.view.component;

public interface View {

    default void initialize() {
        createUiComponents();
        addListeners();
        placeComponents();
    }

    void createUiComponents();

    void addListeners();

    void placeComponents();

    void load();

    void clear();

    default void reload() {
        clear();
        load();
    }
}
