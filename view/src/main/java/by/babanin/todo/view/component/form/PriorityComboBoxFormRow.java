package by.babanin.todo.view.component.form;

import by.babanin.todo.application.service.PriorityService;
import by.babanin.todo.model.Priority;
import by.babanin.todo.representation.ReportField;
import by.babanin.todo.task.GetTask;
import by.babanin.todo.task.TaskManager;
import by.babanin.todo.view.renderer.PriorityComboBoxRenderer;
import by.babanin.todo.view.util.ServiceHolder;

public class PriorityComboBoxFormRow extends ComboBoxFormRow<Priority> {

    private Priority selectedPriority;

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
            setValue(selectedPriority);
        });
        TaskManager.run(task);
    }

    @Override
    public void setValue(Priority value) {
        super.setValue(value);
        this.selectedPriority = value;
    }
}
