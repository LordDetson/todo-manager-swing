package by.babanin.todo.view.component.form;

import java.awt.Dimension;
import java.time.LocalDate;

import javax.swing.JComponent;

import by.babanin.ext.component.datepicker.CustomDatePicker;
import by.babanin.todo.representation.ReportField;

public class LocalDateFormRow extends FormRow<LocalDate> {

    private final CustomDatePicker datePicker;

    public LocalDateFormRow(CustomDatePicker datePicker, ReportField field) {
        super(field);
        this.datePicker = datePicker;
        this.datePicker.setMaximumSize(new Dimension(datePicker.getMaximumSize().width, datePicker.getPreferredSize().height));
        this.datePicker.getSettings().setAllowEmptyDates(!field.isMandatory());
        this.datePicker.addDateChangeListener(dateChangeEvent -> stateChanged());
    }

    @Override
    public JComponent getInputComponent() {
        return datePicker;
    }

    @Override
    public LocalDate getNewValue() {
        return datePicker.getDate();
    }

    @Override
    public void setNewValue(LocalDate value) {
        datePicker.setDate(value);
    }
}
