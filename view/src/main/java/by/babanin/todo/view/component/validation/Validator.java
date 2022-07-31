package by.babanin.todo.view.component.validation;

@FunctionalInterface
public interface Validator {

    ValidationResult validate(Object value);
}
