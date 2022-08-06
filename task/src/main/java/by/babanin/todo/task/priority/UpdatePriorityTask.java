package by.babanin.todo.task.priority;

import java.util.Map;

import by.babanin.todo.application.service.PriorityService;
import by.babanin.todo.model.Priority;
import by.babanin.todo.model.Priority.Fields;
import by.babanin.todo.representation.ComponentRepresentation;
import by.babanin.todo.representation.ReportField;
import by.babanin.todo.task.SaveTask;

public class UpdatePriorityTask extends SaveTask<Priority, Long, PriorityService, Priority> {
    private final Priority oldPriority;

    public UpdatePriorityTask(PriorityService service, ComponentRepresentation<Priority> representation,
            Map<ReportField, ?> fieldValueMap, Priority oldPriority) {
        super(service, representation, fieldValueMap);
        this.oldPriority = oldPriority;
    }

    @Override
    public Priority execute() {
        return getService().rename(oldPriority, getValue(Fields.name));
    }
}
