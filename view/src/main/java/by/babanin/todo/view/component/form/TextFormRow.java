package by.babanin.todo.view.component.form;

import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JTextField;

import org.apache.commons.lang3.StringUtils;

import by.babanin.ext.component.util.GUIUtils;
import by.babanin.todo.representation.ReportField;

public class TextFormRow extends FormRow<String> {

    private static final int DEFAULT_COLUMNS = 16;
    private final JTextField textField;

    public TextFormRow(ReportField field) {
        super(field);
        textField = new JTextField(DEFAULT_COLUMNS);
        textField.setMaximumSize(new Dimension(textField.getMaximumSize().width, textField.getPreferredSize().height));
        GUIUtils.addChangeListener(textField, e -> stateChanged());
    }

    @Override
    public JComponent getInputComponent() {
        return textField;
    }

    @Override
    public String getNewValue() {
        String text = textField.getText().trim();
        if(StringUtils.isBlank(text)) {
            text = null;
        }
        return text;
    }

    @Override
    public void setNewValue(String value) {
        textField.setText(value);
    }
}
