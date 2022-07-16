package by.babanin.todo.view.component.form;

import java.time.LocalDate;

import by.babanin.todo.model.Priority;
import by.babanin.todo.model.Status;
import by.babanin.todo.representation.ReportField;
import by.babanin.todo.view.exception.ViewException;

public class FormRowFactory {

    public FormRow<?> factor(ReportField field) {
        Class<?> type = field.getType();
        if(type.isAssignableFrom(String.class)) {
            return new TextFormRow(field);
        }
        else if(type.isAssignableFrom(LocalDate.class)) {
            return new LocalDateFormRow(field);
        }
        else if(type.isAssignableFrom(Priority.class)) {
            return new PriorityComboBoxFormRow(field);
        }
        else if(type.isAssignableFrom(Status.class)) {
            return new StatusComboBoxFormRow(field);
        }
        throw new ViewException(type.getSimpleName() + " field type doesn't support");
    }
}
