package by.babanin.todo.view.component.form;

import java.awt.Component;
import java.time.LocalDate;

import com.github.lgooddatepicker.components.DatePicker;

import by.babanin.todo.representation.ReportField;

public class LocalDateFormRow extends FormRow<LocalDate> {

    private final DatePicker datePicker;

    public LocalDateFormRow(ReportField field) {
        super(field);
        datePicker = new DatePicker();
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
