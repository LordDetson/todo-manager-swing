package by.babanin.todo.application.status;

import by.babanin.todo.application.exception.ApplicationException;
import by.babanin.todo.model.Status;
import by.babanin.todo.model.Todo;

public interface StatusWorkflow {

    Todo goNextStatus();

    Status getNextStatus();

    boolean isFinalStatus();

    static StatusWorkflow get(Todo todo) {
        return StatusWorkflowFactory.factor(todo);
    }

    static void validateStatus(Todo todo, Status newStatus) {
        Status currentStatus = todo.getStatus();
        StatusWorkflow statusWorkflow = get(todo);
        boolean finalStatus = statusWorkflow.isFinalStatus();
        if(newStatus == null) {
            throw new ApplicationException("Status can't be null");
        }
        else if(currentStatus != newStatus) {
            if(finalStatus) {
                throw new ApplicationException(
                        newStatus + " status can't be applied because " + currentStatus + " current status is finish status");
            }
            else if(newStatus != statusWorkflow.getNextStatus()) {
                throw new ApplicationException(newStatus + " status can't be applied because it isn't next status");
            }
        }
        else if(finalStatus) {
            throw new ApplicationException("\"" + todo.getTitle() + "\" todo is no longer editable because it's final");
        }
    }
}
