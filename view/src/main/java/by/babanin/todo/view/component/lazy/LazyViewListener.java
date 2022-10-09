package by.babanin.todo.view.component.lazy;

import by.babanin.todo.view.component.View;

@FunctionalInterface
public interface LazyViewListener {

    void initialized(View view);
}
