package by.babanin.todo.view.component.form;

import java.awt.Dimension;
import java.time.LocalDate;

import javax.swing.JComponent;

import com.github.lgooddatepicker.components.DatePicker;

import by.babanin.todo.representation.ReportField;
import by.babanin.todo.view.util.GUIUtils;

public class LocalDateFormRow extends FormRow<LocalDate> {

    private final DatePicker datePicker;

    public LocalDateFormRow(ReportField field) {
        super(field);
        datePicker = GUIUtils.createDatePicker();
        datePicker.setMaximumSize(new Dimension(datePicker.getMaximumSize().width, datePicker.getPreferredSize().height));
        datePicker.getSettings().setAllowEmptyDates(!field.isMandatory());
        datePicker.addDateChangeListener(dateChangeEvent -> stateChanged());
    }

    @Override
    public JComponent getInputComponent() {
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
