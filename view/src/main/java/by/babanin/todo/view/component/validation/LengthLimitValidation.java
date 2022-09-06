package by.babanin.todo.view.component.validation;

import org.apache.commons.lang3.StringUtils;

import by.babanin.todo.view.component.logger.LogMessageType;
import by.babanin.todo.view.exception.ViewException;
import by.babanin.todo.view.translat.TranslateCode;
import by.babanin.todo.view.translat.Translator;

public class LengthLimitValidation implements Validator {

    private static final int DEFAULT_MAX_LENGTH = 255;

    private final int maxLength;
    private final String fieldCaption;

    public LengthLimitValidation(String fieldCaption) {
        this(fieldCaption, DEFAULT_MAX_LENGTH);
    }

    public LengthLimitValidation(String fieldCaption, int maxLength) {
        if(StringUtils.isBlank(fieldCaption)) {
            throw new ViewException("fieldCaption can't be blank");
        }
        if(maxLength <= 0) {
            throw new ViewException("maxLength should be more 0");
        }
        this.fieldCaption = fieldCaption;
        this.maxLength = maxLength;
    }

    @Override
    public ValidationResult validate(Object currentValue, Object newValue) {
        ValidationResult result = new ValidationResult();
        if(newValue != null) {
            if(newValue instanceof String name) {
                if(name.length() > maxLength) {
                    String lengthLimitMessage = Translator.toLocale(TranslateCode.LENGTH_LIMIT).formatted(fieldCaption, maxLength);
                    result.put(LogMessageType.ERROR, lengthLimitMessage);
                }
            }
            else {
                throw new ViewException(newValue.getClass().getSimpleName() + " value type should be String");
            }
        }
        return result;
    }
}
