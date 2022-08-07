package by.babanin.todo.application.status;

import by.babanin.todo.model.Status;
import by.babanin.todo.model.Todo;

public class OpenStatusWorkflow extends AbstractStatusWorkflow {

    public OpenStatusWorkflow(Todo todo) {
        super(todo);
    }

    @Override
    public Todo goNextStatus() {
        Todo todo = getTodo();
        todo.setStatus(getNextStatus());
        return todo;
    }

    @Override
    public Status getNextStatus() {
        return Status.IN_PROGRESS;
    }

    @Override
    public boolean isFinalStatus() {
        return false;
    }
}
