package by.babanin.todo.view.component.form;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import by.babanin.todo.representation.ReportField;
import by.babanin.todo.view.translat.Translator;

public abstract class FormRow<T> {

    private final List<ChangeListener> listeners = new ArrayList<>();
    private final ReportField field;
    private final JLabel label;

    protected FormRow(ReportField field) {
        this.field = field;
        String fieldCaption = Translator.getFieldCaption(field);
        this.label = new JLabel(field.isMandatory() ? fieldCaption + " *" : fieldCaption);
    }

    public ReportField getField() {
        return field;
    }

    public JLabel getLabel() {
        return label;
    }

    public abstract Component getInputComponent();

    public abstract T getValue();

    public abstract void setValue(T value);

    public void addListener(ChangeListener listener) {
        listeners.add(listener);
    }

    public void removeListener(ChangeListener listener) {
        listeners.remove(listener);
    }

    protected void stateChanged() {
        listeners.forEach(listener -> listener.stateChanged(new ChangeEvent(getInputComponent())));
    }
}
