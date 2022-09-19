package by.babanin.todo.application.status;

import by.babanin.todo.application.exception.ApplicationException;
import by.babanin.todo.model.Status;
import by.babanin.todo.model.Todo;

public interface StatusWorkflow {

    Todo goNextStatus();

    Status getNextStatus();

    boolean isFinalStatus();

    static StatusWorkflow get(Todo todo) {
        if(todo == null) {
            throw new ApplicationException("Todo is empty");
        }
        return StatusWorkflowFactory.factor(todo);
    }
}
