package by.babanin.todo.view.component.form;

import by.babanin.todo.application.service.PriorityService;
import by.babanin.todo.model.Priority;
import by.babanin.todo.representation.ReportField;
import by.babanin.todo.task.GetTask;
import by.babanin.todo.task.TaskManager;
import by.babanin.todo.view.renderer.PriorityComboBoxRenderer;
import by.babanin.todo.view.util.ServiceHolder;

public class PriorityComboBoxFormRow extends ComboBoxFormRow<Priority> {

    public PriorityComboBoxFormRow(ReportField field) {
        super(field);
        setAllowNone(true);
        setRenderer(new PriorityComboBoxRenderer());
        loadPriorities();
    }

    private void loadPriorities() {
        GetTask<Priority, Long, PriorityService> task = new GetTask<>(ServiceHolder.getPriorityService());
        task.addFinishListener(items -> {
            setItems(items);
            ignoreStatusChange();
            setNewValue(getCurrentValue());
            enableStatusChange();
        });
        TaskManager.run(task);
    }
}
