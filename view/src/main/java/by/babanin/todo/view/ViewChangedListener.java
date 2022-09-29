package by.babanin.todo.view;

import by.babanin.todo.view.component.View;

@FunctionalInterface
public interface ViewChangedListener {

    void viewChanged(String viewName, View view);
}
