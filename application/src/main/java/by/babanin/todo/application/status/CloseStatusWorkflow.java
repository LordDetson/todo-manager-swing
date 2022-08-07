package by.babanin.todo.application.status;

import by.babanin.todo.application.exception.ApplicationException;
import by.babanin.todo.model.Status;
import by.babanin.todo.model.Todo;

public class CloseStatusWorkflow extends AbstractStatusWorkflow {

    protected CloseStatusWorkflow(Todo todo) {
        super(todo);
    }

    @Override
    public Todo goNextStatus() {
        throw createFinalException();
    }

    @Override
    public Status getNextStatus() {
        throw createFinalException();
    }

    @Override
    public boolean isFinalStatus() {
        return true;
    }

    public ApplicationException createFinalException() {
        return new ApplicationException("Closed status is the final status");
    }
}
