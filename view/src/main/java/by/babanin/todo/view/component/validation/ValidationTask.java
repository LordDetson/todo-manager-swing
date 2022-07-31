package by.babanin.todo.view.component.validation;

import java.util.ArrayList;
import java.util.List;

import by.babanin.todo.task.AbstractTask;

public class ValidationTask extends AbstractTask<List<ValidationResult>> {

    private final Object value;
    private final List<Validator> validators = new ArrayList<>();

    public ValidationTask(Object value, List<Validator> validators) {
        this.value = value;
        this.validators.addAll(validators);
    }

    @Override
    public List<ValidationResult> execute() {
        return validators.stream()
                .map(validator -> validator.validate(value))
                .filter(ValidationResult::isNotEmpty)
                .toList();
    }
}
