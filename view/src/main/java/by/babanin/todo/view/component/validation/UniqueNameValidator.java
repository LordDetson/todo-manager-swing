package by.babanin.todo.view.component.validation;

import java.util.function.Function;

import by.babanin.ext.message.Translator;
import by.babanin.todo.view.component.logger.LogMessageType;
import by.babanin.todo.view.exception.ViewException;
import by.babanin.todo.view.translat.AppTranslateCode;
import by.babanin.todo.view.translat.AppTranslator;

public class UniqueNameValidator implements Validator {

    private final Class<?> componentType;
    private final Function<String, Boolean> existsNameFunction;

    public UniqueNameValidator(Class<?> componentType, Function<String, Boolean> existsNameFunction) {
        this.componentType = componentType;
        this.existsNameFunction = existsNameFunction;
    }

    @Override
    public ValidationResult validate(Object currentValue, Object newValue) {
        ValidationResult result = new ValidationResult();
        if(newValue != null && (currentValue == null || !currentValue.equals(newValue))) {
            if(newValue instanceof String name) {
                if(Boolean.TRUE.equals(existsNameFunction.apply(name))) {
                    String uniqueNameMessage = Translator.toLocale(AppTranslateCode.UNIQUE_NAME)
                            .formatted(name, AppTranslator.getComponentCaption(componentType).toLowerCase());
                    result.put(LogMessageType.ERROR, uniqueNameMessage);
                }
            }
            else {
                throw new ViewException(newValue.getClass().getSimpleName() + " value type should be String");
            }
        }
        return result;
    }
}
