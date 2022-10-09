package by.babanin.todo.view.priority;

import java.awt.Component;
import java.util.Map;

import by.babanin.todo.application.service.PriorityService;
import by.babanin.todo.model.Priority;
import by.babanin.todo.representation.ComponentRepresentation;
import by.babanin.todo.representation.ReportField;
import by.babanin.todo.task.Task;
import by.babanin.todo.task.priority.CreatePriorityTask;
import by.babanin.todo.task.priority.UpdatePriorityTask;
import by.babanin.todo.view.component.CrudStyle;
import by.babanin.todo.view.component.crud.Crud;
import by.babanin.todo.view.component.form.FormRowFactory;

public class PriorityCrud extends Crud<Priority, Long, PriorityService> {

    public PriorityCrud(Component owner, PriorityService crudService,
            ComponentRepresentation<Priority> representation,
            FormRowFactory formRowFactory, CrudStyle crudStyle) {
        super(owner, crudService, representation, formRowFactory, crudStyle);
    }

    @Override
    protected Task<Priority> createCreationTask(Map<ReportField, ?> fieldValueMap) {
        return new CreatePriorityTask(getCrudService(), getRepresentation(), fieldValueMap);
    }

    @Override
    protected Task<Priority> createUpdateTask(Map<ReportField, ?> fieldValueMap, Priority priority) {
        return new UpdatePriorityTask(getCrudService(), getRepresentation(), fieldValueMap, priority);
    }
}
