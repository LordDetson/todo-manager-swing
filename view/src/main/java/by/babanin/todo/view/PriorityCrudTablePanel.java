package by.babanin.todo.view;

import java.util.Map;

import by.babanin.todo.application.service.PriorityService;
import by.babanin.todo.model.Priority;
import by.babanin.todo.representation.ReportField;
import by.babanin.todo.task.Task;
import by.babanin.todo.task.priority.CreatePriorityTask;
import by.babanin.todo.task.priority.UpdatePriorityTask;
import by.babanin.todo.view.component.CrudStyle;
import by.babanin.todo.view.component.MovableCrudTablePanel;
import by.babanin.todo.view.component.form.PriorityFormRowFactory;
import by.babanin.todo.view.component.validation.PriorityValidatorFactory;
import by.babanin.todo.view.util.ServiceHolder;

public class PriorityCrudTablePanel extends MovableCrudTablePanel<Priority, Long> {

    public PriorityCrudTablePanel() {
        super(Priority.class, new PriorityFormRowFactory(), new CrudStyle()
                .setValidatorFactory(new PriorityValidatorFactory()));
    }

    @Override
    protected Task<Priority> createCreationTask(Map<ReportField, ?> fieldValueMap) {
        PriorityService service = ServiceHolder.getPriorityService();
        return new CreatePriorityTask(service, getRepresentation(), fieldValueMap);
    }

    @Override
    protected Task<Priority> createUpdateTask(Map<ReportField, ?> fieldValueMap, Priority selectedComponent) {
        PriorityService service = ServiceHolder.getPriorityService();
        return new UpdatePriorityTask(service, getRepresentation(), fieldValueMap, selectedComponent);
    }
}
