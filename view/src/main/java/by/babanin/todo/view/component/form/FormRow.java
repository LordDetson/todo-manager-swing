package by.babanin.todo.view.component.form;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.formdev.flatlaf.FlatClientProperties;

import by.babanin.todo.representation.ReportField;
import by.babanin.todo.task.TaskManager;
import by.babanin.todo.view.component.logger.LogMessageType;
import by.babanin.todo.view.component.logger.Logger;
import by.babanin.todo.view.component.validation.ValidationResult;
import by.babanin.todo.view.component.validation.ValidationTask;
import by.babanin.todo.view.component.validation.Validator;

public abstract class FormRow<T> {

    private final List<ChangeListener> listeners = new ArrayList<>();
    private final List<Validator> validators = new ArrayList<>();
    private final Logger logger = new Logger();
    private final ReportField field;
    private final JLabel label;

    protected FormRow(ReportField field) {
        this.field = field;
        String fieldCaption = field.getCaption();
        this.label = new JLabel(field.isMandatory() ? fieldCaption + " *" : fieldCaption);
        addListener(this::applyOutlineProperty);
    }

    public ReportField getField() {
        return field;
    }

    public JLabel getLabel() {
        return label;
    }

    public abstract JComponent getInputComponent();

    public abstract T getValue();

    public abstract void setValue(T value);

    public void addListener(ChangeListener listener) {
        listeners.add(listener);
    }

    public void removeListener(ChangeListener listener) {
        listeners.remove(listener);
    }

    protected void stateChanged() {
        validate(getValue());
    }

    public Logger getLogger() {
        return logger;
    }

    public void addValidators(List<Validator> validators) {
        this.validators.addAll(validators);
    }

    public void validate(Object value) {
        if(!validators.isEmpty()) {
            ValidationTask task = new ValidationTask(value, validators);
            task.addFinishListener(this::handleValidationResults);
            TaskManager.run(task);
        }
    }

    private void handleValidationResults(List<ValidationResult> validationResults) {
        logger.clear();
        validationResults.forEach(logger::logBatchMessages);
        boolean valid = validationResults.stream()
                .noneMatch(result -> result.hasMessages(LogMessageType.ERROR));
        boolean hasWarning = validationResults.stream()
                .anyMatch(result -> result.hasMessages(LogMessageType.WARNING));
        InputEvent event = new InputEvent(getInputComponent());
        event.setValid(valid);
        event.setHasWarning(hasWarning);
        listeners.forEach(listener -> listener.stateChanged(event));
    }

    private void applyOutlineProperty(ChangeEvent event) {
        if(event instanceof InputEvent inputEvent) {
            String outlinePropertyValue = null;
            if(!inputEvent.isValid()) {
                outlinePropertyValue = FlatClientProperties.OUTLINE_ERROR;
            }
            else if(inputEvent.hasWarning()) {
                outlinePropertyValue = FlatClientProperties.OUTLINE_WARNING;
            }
            getInputComponent().putClientProperty(FlatClientProperties.OUTLINE, outlinePropertyValue);
        }
    }
}
