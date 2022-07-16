package by.babanin.todo.view.component.form;

import java.util.List;

import by.babanin.todo.model.Status;
import by.babanin.todo.representation.ReportField;
import by.babanin.todo.view.renderer.StatusComboBoxRenderer;

public class StatusComboBoxFormRow extends ComboBoxFormRow<Status> {

    public StatusComboBoxFormRow(ReportField field) {
        super(field);
        setRenderer(new StatusComboBoxRenderer());
        setItems(List.of(Status.values()));
    }
}
