package by.babanin.todo.view.component.form;

import java.time.LocalDate;

import com.github.lgooddatepicker.zinternaltools.DateVetoPolicyMinimumMaximumDate;

import by.babanin.todo.application.service.PriorityService;
import by.babanin.todo.model.Todo;
import by.babanin.todo.model.Todo.Fields;
import by.babanin.todo.representation.ComponentRepresentation;
import by.babanin.todo.representation.ReportField;
import by.babanin.todo.view.component.datepicker.CustomDatePicker;
import by.babanin.todo.view.exception.ViewException;

public class TodoFormRowFactory implements FormRowFactory {

    private final PriorityService priorityService;

    public TodoFormRowFactory(PriorityService priorityService) {
        this.priorityService = priorityService;
    }

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
                formRow = new PriorityComboBoxFormRow(priorityService, field);
            }
            else if(fieldName.equals(Fields.status)) {
                formRow = new StatusComboBoxFormRow(field);
            }
            else if(fieldName.equals(Fields.plannedDate)) {
                CustomDatePicker datePicker = new CustomDatePicker();
                datePicker.getSettings().setVetoPolicy(new DateVetoPolicyMinimumMaximumDate(LocalDate.now(), LocalDate.MAX));
                formRow = new LocalDateFormRow(datePicker, field);
            }
            return (FormRow<Object>) formRow;
        }
        else {
            throw new ViewException(
                    field.getName() + " field of " + representation.getComponentClass().getSimpleName() + "class doesn't support");
        }
    }
}
