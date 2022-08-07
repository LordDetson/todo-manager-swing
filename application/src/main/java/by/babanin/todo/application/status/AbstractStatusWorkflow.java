package by.babanin.todo.application.status;

import by.babanin.todo.model.Todo;

public abstract class AbstractStatusWorkflow implements StatusWorkflow {

    private final Todo todo;

    protected AbstractStatusWorkflow(Todo todo) {
        this.todo = todo;
    }

    protected Todo getTodo() {
        return todo;
    }
}
