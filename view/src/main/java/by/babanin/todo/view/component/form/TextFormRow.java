package by.babanin.todo.view.component.form;

import java.awt.Component;

import javax.swing.JTextField;

import by.babanin.todo.representation.ReportField;
import by.babanin.todo.view.util.GUIUtils;

public class TextFormRow extends FormRow<String> {

    private final JTextField textField;

    public TextFormRow(ReportField field) {
        super(field);
        textField = new JTextField();
        GUIUtils.addChangeListener(textField, e -> stateChanged());
    }

    @Override
    public Component getInputComponent() {
        return textField;
    }

    @Override
    public String getValue() {
        return textField.getText();
    }

    @Override
    public void setValue(String value) {
        textField.setText(value);
    }
}
