package by.babanin.todo.view.component.form;

import java.util.List;

import by.babanin.todo.application.status.StatusWorkflow;
import by.babanin.todo.model.Status;
import by.babanin.todo.model.Todo;
import by.babanin.todo.representation.ReportField;
import by.babanin.todo.view.renderer.StatusComboBoxRenderer;

public class StatusComboBoxFormRow extends ComboBoxFormRow<Status> {

    public StatusComboBoxFormRow(ReportField field) {
        super(field);
        setRenderer(new StatusComboBoxRenderer());
    }

    @Override
    public void setComponent(Object component) {
        super.setComponent(component);
        if(component instanceof Todo todo) {
            Status current = todo.getStatus();
            Status nextStatus = StatusWorkflow.get(todo).getNextStatus();
            setItems(List.of(current, nextStatus));
        }
    }
}
