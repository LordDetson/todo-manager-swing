package by.babanin.todo.task.priority;

import java.util.Map;

import by.babanin.todo.application.service.PriorityService;
import by.babanin.todo.model.Priority;
import by.babanin.todo.model.Priority.Fields;
import by.babanin.todo.representation.ComponentRepresentation;
import by.babanin.todo.representation.ReportField;
import by.babanin.todo.task.SaveTask;

public class CreatePriorityTask extends SaveTask<Priority, Long, PriorityService, Priority> {

    public CreatePriorityTask(PriorityService priorityService, ComponentRepresentation<Priority> representation,
            Map<ReportField, ?> fieldValueMap) {
        super(priorityService, representation, fieldValueMap);
    }

    @Override
    public Priority execute() {
        return getService().create(getValue(Fields.name));
    }
}
