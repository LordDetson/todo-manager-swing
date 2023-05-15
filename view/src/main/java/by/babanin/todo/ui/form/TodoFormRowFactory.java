package by.babanin.todo.ui.form;

import java.time.LocalDate;

import org.modelmapper.ModelMapper;

import com.github.lgooddatepicker.zinternaltools.DateVetoPolicyMinimumMaximumDate;

import by.babanin.ext.component.datepicker.CustomDatePicker;
import by.babanin.ext.component.exception.UIException;
import by.babanin.ext.component.form.FormRow;
import by.babanin.ext.component.form.FormRowFactory;
import by.babanin.ext.component.form.LocalDateFormRow;
import by.babanin.ext.component.form.TextAreaFormRow;
import by.babanin.ext.component.form.TextFormRow;
import by.babanin.ext.representation.ReportField;
import by.babanin.ext.representation.Representation;
import by.babanin.ext.representation.RepresentationRegister;
import by.babanin.todo.application.service.PriorityService;
import by.babanin.todo.ui.dto.ToDoInfo;

public class TodoFormRowFactory implements FormRowFactory {

    private final PriorityService priorityService;
    private final ModelMapper modelMapper;

    public TodoFormRowFactory(PriorityService priorityService, ModelMapper modelMapper) {
        this.priorityService = priorityService;
        this.modelMapper = modelMapper;
    }

    @SuppressWarnings("unchecked")
    @Override
    public FormRow<Object> factor(ReportField field) {
        Representation<ToDoInfo> representation = RepresentationRegister.get(ToDoInfo.class);
        if(representation.getFields().contains(field)) {
            String fieldName = field.getName();
            FormRow<?> formRow = null;
            if(fieldName.equals(ToDoInfo.Fields.title)) {
                formRow = new TextFormRow(field);
            }
            else if(fieldName.equals(ToDoInfo.Fields.description)) {
                formRow = new TextAreaFormRow(field);
            }
            else if(fieldName.equals(ToDoInfo.Fields.priority)) {
                formRow = new PriorityComboBoxFormRow(field, priorityService, modelMapper);
            }
            else if(fieldName.equals(ToDoInfo.Fields.status)) {
                formRow = new StatusComboBoxFormRow(field, modelMapper);
            }
            else if(fieldName.equals(ToDoInfo.Fields.plannedDate)) {
                CustomDatePicker datePicker = new CustomDatePicker();
                datePicker.getSettings().setVetoPolicy(new DateVetoPolicyMinimumMaximumDate(LocalDate.now(), LocalDate.MAX));
                formRow = new LocalDateFormRow(datePicker, field);
            }
            return (FormRow<Object>) formRow;
        }
        else {
            throw new UIException(
                    field.getName() + " field of " + representation.getComponentClass().getSimpleName() + "class doesn't support");
        }
    }
}
