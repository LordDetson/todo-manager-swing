package by.babanin.todo.view.component.validation;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import by.babanin.ext.message.Translator;
import by.babanin.todo.view.component.logger.LogMessageType;
import by.babanin.todo.view.exception.ViewException;
import by.babanin.todo.view.translat.AppTranslateCode;

public class ForbiddenSymbolsValidator implements Validator {

    private final String fieldCaption;
    private final List<String> forbiddenSymbols;

    public ForbiddenSymbolsValidator(String fieldCaption, List<String> forbiddenSymbols) {
        if(StringUtils.isBlank(fieldCaption)) {
            throw new ViewException("fieldCaption can't be blank");
        }
        if(forbiddenSymbols == null || forbiddenSymbols.isEmpty()) {
            throw new ViewException("forbiddenSymbols can't be blank");
        }
        this.fieldCaption = fieldCaption;
        this.forbiddenSymbols = forbiddenSymbols;
    }

    @Override
    public ValidationResult validate(Object currentValue, Object newValue) {
        ValidationResult result = new ValidationResult();
        if(newValue != null) {
            if(newValue instanceof String name) {
                if(containsForbiddenSymbol(name)) {
                    String forbiddenSymbolsMessage = Translator.toLocale(AppTranslateCode.FORBIDDEN_SYMBOLS)
                            .formatted(fieldCaption, forbiddenSymbols);
                    result.put(LogMessageType.ERROR, forbiddenSymbolsMessage);
                }
            }
            else {
                throw new ViewException(newValue.getClass().getSimpleName() + " value type should be String");
            }
        }
        return result;
    }

    private boolean containsForbiddenSymbol(String name) {
        if(StringUtils.isNotBlank(name)) {
            for(String forbiddenSymbol : forbiddenSymbols) {
                if(name.contains(forbiddenSymbol)) {
                    return true;
                }
            }
        }
        return false;
    }
}
