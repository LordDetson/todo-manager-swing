package by.babanin.todo.view.component.form;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import by.babanin.todo.representation.ReportField;
import by.babanin.todo.view.util.GUIUtils;

public class TextAreaFormRow extends FormRow<String> {

    private static final int DEFAULT_COLUMNS = 32;
    private static final int DEFAULT_ROWS = 8;

    private final JTextArea textArea;
    private final JScrollPane scrollPane;

    public TextAreaFormRow(ReportField field) {
        this(field, DEFAULT_ROWS, DEFAULT_COLUMNS);
    }

    public TextAreaFormRow(ReportField field, int rows, int columns) {
        super(field);
        textArea = new JTextArea(rows, columns);
        scrollPane = new JScrollPane(textArea);
        GUIUtils.addChangeListener(textArea, e -> stateChanged());
    }

    @Override
    public JComponent getInputComponent() {
        return scrollPane;
    }

    @Override
    public String getValue() {
        return textArea.getText();
    }

    @Override
    public void setValue(String value) {
        textArea.setText(value);
        textArea.setCaretPosition(0);
    }
}
