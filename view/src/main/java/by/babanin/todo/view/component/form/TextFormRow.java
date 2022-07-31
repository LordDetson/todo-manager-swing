package by.babanin.todo.view.component.form;

import javax.swing.JComponent;
import javax.swing.JTextField;

import org.apache.commons.lang3.StringUtils;

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
    public JComponent getInputComponent() {
        return textField;
    }

    @Override
    public String getValue() {
        String text = textField.getText().trim();
        if(StringUtils.isBlank(text)) {
            text = null;
        }
        return text;
    }

    @Override
    public void setValue(String value) {
        textField.setText(value);
    }
}
