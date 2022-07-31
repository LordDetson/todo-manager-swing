package by.babanin.todo.view.component.validation;

import org.apache.commons.lang3.StringUtils;

import by.babanin.todo.view.component.logger.LogMessageType;
import by.babanin.todo.view.exception.ViewException;

public class MandatoryValueValidator implements Validator {

    private final String fieldCaption;

    public MandatoryValueValidator(String fieldCaption) {
        if(StringUtils.isBlank(fieldCaption)) {
            throw new ViewException("fieldCaption can't be blank");
        }
        this.fieldCaption = fieldCaption;
    }

    @Override
    public ValidationResult validate(Object value) {
        ValidationResult result = new ValidationResult();
        if(value == null) {
            result.put(LogMessageType.ERROR, fieldCaption + " can't be empty");
        }
        return result;
    }
}
