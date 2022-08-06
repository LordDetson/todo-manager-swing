package by.babanin.todo.view.component.validation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringJoiner;

import by.babanin.todo.view.component.logger.LogMessageType;

public class ReservedWordsValidator implements Validator {

    private final List<String> reservedWords = new ArrayList<>();

    public ReservedWordsValidator(Collection<String> reservedWords) {
        this.reservedWords.addAll(reservedWords);
    }

    @Override
    public ValidationResult validate(Object value) {
        ValidationResult result = new ValidationResult();
        if(value instanceof String name && reservedWords.contains(name)) {
            StringJoiner joiner = new StringJoiner(", ");
            reservedWords.forEach(joiner::add);
            result.put(LogMessageType.ERROR, "\"" + name + "\" is forbidden to use. Reserved words: " + joiner);
        }
        return result;
    }
}
