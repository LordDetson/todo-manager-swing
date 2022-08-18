package by.babanin.todo.view.component.datepicker;

import javax.swing.JTextField;

import com.github.lgooddatepicker.components.DatePicker;

public class CustomDatePicker extends DatePicker {

    public CustomDatePicker() {
        this(new CustomDatePickerSettings());
    }

    public CustomDatePicker(CustomDatePickerSettings settings) {
        super(settings);
        getComponentDateTextField().setBorder(new JTextField().getBorder());
    }
}
