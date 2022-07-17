package by.babanin.todo.view.component.form;

import java.awt.Component;
import java.time.LocalDate;

import com.github.lgooddatepicker.components.DatePicker;

import by.babanin.todo.representation.ReportField;
import by.babanin.todo.view.util.GUIUtils;

public class LocalDateFormRow extends FormRow<LocalDate> {

    private final DatePicker datePicker;

    public LocalDateFormRow(ReportField field) {
        super(field);
        datePicker = GUIUtils.createDatePicker();
        datePicker.getSettings().setAllowEmptyDates(!field.isMandatory());
        datePicker.addDateChangeListener(dateChangeEvent -> stateChanged());
    }

    @Override
    public Component getInputComponent() {
        return datePicker;
    }

    @Override
    public LocalDate getValue() {
        return datePicker.getDate();
    }

    @Override
    public void setValue(LocalDate value) {
        datePicker.setDate(value);
    }
}
