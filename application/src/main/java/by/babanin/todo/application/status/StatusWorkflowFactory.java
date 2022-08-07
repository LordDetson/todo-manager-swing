package by.babanin.todo.application.status;

import by.babanin.todo.model.Status;
import by.babanin.todo.model.Todo;
import lombok.experimental.UtilityClass;

@UtilityClass
public class StatusWorkflowFactory {

    public static StatusWorkflow factor(Todo todo) {
        Status status = todo.getStatus();
        return switch(status) {
            case OPEN -> new OpenStatusWorkflow(todo);
            case IN_PROGRESS -> new InProgressStatusWorkflow(todo);
            case CLOSED -> new CloseStatusWorkflow(todo);
        };
    }
}
