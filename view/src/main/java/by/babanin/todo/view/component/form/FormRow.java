package by.babanin.todo.view.component.form;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
import by.babanin.todo.view.translat.Translator;

public abstract class FormRow<T> {

    private final List<ChangeListener> listeners = new ArrayList<>();
    private final List<Validator> validators = new ArrayList<>();
    private final Logger logger = new Logger();
    private final ReportField field;
    private final JLabel label;
    private Object component;

    protected FormRow(ReportField field) {
        this.field = field;
        String fieldCaption = Translator.getFieldCaption(field);
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

    @SuppressWarnings("unchecked")
    public T getCurrentValue() {
        if(component != null) {
            return (T) field.getValue(component);
        }
        return null;
    }

    public abstract T getNewValue();

    public abstract void setNewValue(T value);

    public void setComponent(Object component) {
        this.component = component;
        setNewValue(getCurrentValue());
    }

    public Optional<Object> getComponent() {
        return Optional.ofNullable(component);
    }

    public void addListener(ChangeListener listener) {
        listeners.add(listener);
    }

    public void removeListener(ChangeListener listener) {
        listeners.remove(listener);
    }

    protected void stateChanged() {
        validate(getCurrentValue(), getNewValue());
    }

    public Logger getLogger() {
        return logger;
    }

    public void addValidators(List<Validator> validators) {
        this.validators.addAll(validators);
    }

    public void validate(T currentValue, T newValue) {
        if(!validators.isEmpty()) {
            ValidationTask task = new ValidationTask(currentValue, newValue, validators);
            task.addFinishListener(this::handleValidationResults);
            TaskManager.run(task);
        }
        else {
            fireInputEvent(true, false);
        }
    }

    private void handleValidationResults(List<ValidationResult> validationResults) {
        logger.clear();
        validationResults.forEach(logger::logBatchMessages);
        boolean valid = validationResults.stream()
                .noneMatch(result -> result.hasMessages(LogMessageType.ERROR));
        boolean hasWarning = validationResults.stream()
                .anyMatch(result -> result.hasMessages(LogMessageType.WARNING));
        fireInputEvent(valid, hasWarning);
    }

    private void fireInputEvent(boolean valid, boolean hasWarning) {
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
