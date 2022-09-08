package by.babanin.todo.view.component.validation;

import org.apache.commons.lang3.StringUtils;

import by.babanin.todo.view.component.logger.LogMessageType;
import by.babanin.todo.view.exception.ViewException;
import by.babanin.todo.view.translat.TranslateCode;
import by.babanin.todo.view.translat.Translator;

public class AsciiPrintableValidator implements Validator {

    private final String fieldCaption;

    public AsciiPrintableValidator(String fieldCaption) {
        this.fieldCaption = fieldCaption;
    }

    @Override
    public ValidationResult validate(Object currentValue, Object newValue) {
        ValidationResult result = new ValidationResult();
        if(newValue != null) {
            if(newValue instanceof String str) {
                if(!StringUtils.isAsciiPrintable(str)) {
                    String message = Translator.toLocale(TranslateCode.NON_ASCII_PRINTABLE).formatted(fieldCaption);
                    result.put(LogMessageType.WARNING, message);
                }
            }
            else {
                throw new ViewException(newValue.getClass().getSimpleName() + " value type should be String");
            }
        }
        return result;
    }
}
