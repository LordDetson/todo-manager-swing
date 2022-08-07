package by.babanin.todo.application.status;

import java.time.LocalDate;

import by.babanin.todo.model.Status;
import by.babanin.todo.model.Todo;

public class InProgressStatusWorkflow extends AbstractStatusWorkflow {

    protected InProgressStatusWorkflow(Todo todo) {
        super(todo);
    }

    @Override
    public Todo goNextStatus() {
        Todo todo = getTodo();
        todo.setStatus(getNextStatus());
        todo.setCompletionDate(LocalDate.now());
        return todo;
    }

    @Override
    public Status getNextStatus() {
        return Status.CLOSED;
    }

    @Override
    public boolean isFinalStatus() {
        return false;
    }
}
