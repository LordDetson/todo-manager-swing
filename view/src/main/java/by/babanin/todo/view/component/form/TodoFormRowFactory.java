package by.babanin.todo.view.component.form;

import by.babanin.todo.model.Todo;
import by.babanin.todo.model.Todo.Fields;
import by.babanin.todo.representation.ComponentRepresentation;
import by.babanin.todo.representation.ReportField;
import by.babanin.todo.view.exception.ViewException;

public class TodoFormRowFactory implements FormRowFactory {

    @SuppressWarnings("unchecked")
    @Override
    public FormRow<Object> factor(ReportField field) {
        ComponentRepresentation<Todo> representation = ComponentRepresentation.get(Todo.class);
        if(representation.getFields().contains(field)) {
            String fieldName = field.getName();
            FormRow<?> formRow = null;
            if(fieldName.equals(Fields.title)) {
                formRow = new TextFormRow(field);
            }
            else if(fieldName.equals(Fields.description)) {
                formRow = new TextAreaFormRow(field);
            }
            else if(fieldName.equals(Fields.priority)) {
                formRow = new PriorityComboBoxFormRow(field);
            }
            else if(fieldName.equals(Fields.status)) {
                formRow = new StatusComboBoxFormRow(field);
            }
            else if(fieldName.equals(Fields.plannedDate)) {
                formRow = new LocalDateFormRow(field);
            }
            return (FormRow<Object>) formRow;
        }
        else {
            throw new ViewException(
                    field.getName() + " field of " + representation.getComponentClass().getSimpleName() + "class doesn't support");
        }
    }
}