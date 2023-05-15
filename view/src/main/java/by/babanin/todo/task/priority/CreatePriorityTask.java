package by.babanin.todo.task.priority;

import java.util.Map;

import org.modelmapper.ModelMapper;

import by.babanin.ext.representation.Representation;
import by.babanin.ext.representation.ReportField;
import by.babanin.todo.application.service.PriorityService;
import by.babanin.todo.model.Priority;
import by.babanin.todo.model.Priority.Fields;
import by.babanin.todo.task.SaveTask;
import by.babanin.todo.ui.dto.PriorityInfo;

public class CreatePriorityTask extends SaveTask<Priority, Long, PriorityService, PriorityInfo> {

    public CreatePriorityTask(PriorityService priorityService, ModelMapper modelMapper, Representation<PriorityInfo> representation,
            Map<ReportField, ?> fieldValueMap) {
        super(priorityService, modelMapper, representation, fieldValueMap);
    }

    @Override
    public Priority body() {
        return getService().create(getValue(Fields.name));
    }
}
