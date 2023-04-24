package by.babanin.todo.view;

import java.util.Map;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

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
import by.babanin.todo.view.settings.Settings;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public final class PriorityCrudTablePanel extends MovableCrudTablePanel<Priority, Long> {

    public PriorityCrudTablePanel(PriorityService priorityService, Settings settings) {
        super(priorityService, Priority.class, new PriorityFormRowFactory(), settings, new CrudStyle()
                .setValidatorFactory(new PriorityValidatorFactory(priorityService)));
        setName("priorityCrudTablePanel");
    }

    @Override
    protected PriorityService getService() {
        return (PriorityService) super.getService();
    }

    @Override
    protected Task<Priority> createCreationTask(Map<ReportField, ?> fieldValueMap) {
        return new CreatePriorityTask(getService(), getRepresentation(), fieldValueMap);
    }

    @Override
    protected Task<Priority> createUpdateTask(Map<ReportField, ?> fieldValueMap, Priority selectedComponent) {
        return new UpdatePriorityTask(getService(), getRepresentation(), fieldValueMap, selectedComponent);
    }
}
