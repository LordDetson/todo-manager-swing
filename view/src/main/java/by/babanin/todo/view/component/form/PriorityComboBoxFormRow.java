package by.babanin.todo.view.component.form;

import by.babanin.todo.application.service.PriorityService;
import by.babanin.todo.model.Priority;
import by.babanin.todo.representation.ReportField;
import by.babanin.todo.task.GetTask;
import by.babanin.todo.view.renderer.PriorityComboBoxRenderer;

public class PriorityComboBoxFormRow extends ComboBoxFormRow<Priority> {

    private final PriorityService priorityService;

    public PriorityComboBoxFormRow(PriorityService priorityService, ReportField field) {
        super(field);
        this.priorityService = priorityService;
        setAllowNone(true);
        setRenderer(new PriorityComboBoxRenderer());
        loadPriorities();
    }

    private void loadPriorities() {
        GetTask<Priority, Long, PriorityService> task = new GetTask<>(priorityService);
        task.addFinishListener(items -> {
            setItems(items);
            ignoreStatusChange();
            setNewValue(getCurrentValue());
            enableStatusChange();
        });
        task.execute();
    }
}
