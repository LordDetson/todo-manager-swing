package by.babanin.todo.view.component;

import java.io.Serializable;

import by.babanin.todo.view.exception.ViewException;

public interface Initializable extends Serializable {

    default void initialize() {
        if(isInitialized()) {
            throw new ViewException("It's already initialized");
        }
        createUiComponents();
        addListeners();
        placeComponents();
        setInitialized(true);
    }

    boolean isInitialized();

    void setInitialized(boolean initialized);

    void createUiComponents();

    void addListeners();

    void placeComponents();
}
