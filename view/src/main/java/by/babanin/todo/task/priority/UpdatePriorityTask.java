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

public class UpdatePriorityTask extends SaveTask<Priority, Long, PriorityService, PriorityInfo> {

    private final PriorityInfo oldPriority;

    public UpdatePriorityTask(PriorityService service, ModelMapper modelMapper, Representation<PriorityInfo> representation,
            Map<ReportField, ?> fieldValueMap, PriorityInfo oldPriority) {
        super(service, modelMapper, representation, fieldValueMap);
        this.oldPriority = oldPriority;
    }

    @Override
    public Priority body() {
        return getService().rename(oldPriority.getId(), getValue(Fields.name));
    }
}
