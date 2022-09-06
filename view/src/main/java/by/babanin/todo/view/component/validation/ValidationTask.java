package by.babanin.todo.view.component.validation;

import java.util.ArrayList;
import java.util.List;

import by.babanin.todo.task.AbstractTask;

public class ValidationTask extends AbstractTask<List<ValidationResult>> {

    private final Object currentValue;
    private final Object newValue;
    private final List<Validator> validators = new ArrayList<>();

    public ValidationTask(Object currentValue, Object newValue, List<Validator> validators) {
        this.currentValue = currentValue;
        this.newValue = newValue;
        this.validators.addAll(validators);
    }

    @Override
    public List<ValidationResult> execute() {
        return validators.stream()
                .map(validator -> validator.validate(currentValue, newValue))
                .filter(ValidationResult::isNotEmpty)
                .toList();
    }
}
