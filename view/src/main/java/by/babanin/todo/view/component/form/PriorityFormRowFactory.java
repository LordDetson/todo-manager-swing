package by.babanin.todo.view.component.form;

import by.babanin.todo.model.Priority;
import by.babanin.todo.model.Priority.Fields;
import by.babanin.todo.representation.ComponentRepresentation;
import by.babanin.todo.representation.ReportField;
import by.babanin.todo.view.exception.ViewException;

public class PriorityFormRowFactory implements FormRowFactory {

    @SuppressWarnings("unchecked")
    @Override
    public FormRow<Object> factor(ReportField field) {
        ComponentRepresentation<Priority> representation = ComponentRepresentation.get(Priority.class);
        if(representation.getFields().contains(field) && field.getName().equals(Fields.name)) {
            FormRow<?> formRow = new TextFormRow(field);
            return (FormRow<Object>) formRow;
        }
        else {
            throw new ViewException(field.getName() + " field of " + representation.getComponentClass().getSimpleName() + "class doesn't support");
        }
    }
}
