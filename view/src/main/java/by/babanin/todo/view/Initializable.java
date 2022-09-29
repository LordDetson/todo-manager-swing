package by.babanin.todo.view;

import java.io.Serializable;

public interface Initializable extends Serializable {

    default void initialize() {
        createUiComponents();
        addListeners();
        placeComponents();
    }

    void createUiComponents();

    void addListeners();

    void placeComponents();
}
