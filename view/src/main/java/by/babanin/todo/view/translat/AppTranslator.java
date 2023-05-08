package by.babanin.todo.view.translat;

import by.babanin.ext.message.Translator;
import by.babanin.todo.model.Status;
import by.babanin.todo.representation.ReportField;
import lombok.experimental.UtilityClass;

@UtilityClass
public class AppTranslator {

    public static String getStatusCaption(Status status) {
        return Translator.toLocale(String.format(AppTranslateCode.TODO_STATUS, status.name().toLowerCase()));
    }

    public static String getFieldCaption(ReportField field) {
        return Translator.toLocale(String.format(AppTranslateCode.FIELD_CAPTION, field.getDeclaringClass().getSimpleName(), field.getName()));
    }

    public static <T> String getComponentCaption(Class<T> componentClass) {
        return Translator.toLocale(String.format(AppTranslateCode.COMPONENT_CAPTION, componentClass.getSimpleName()));
    }

    public static <T> String getComponentPluralCaption(Class<T> componentClass) {
        return Translator.toLocale(String.format(AppTranslateCode.COMPONENT_PLURAL_CAPTION, componentClass.getSimpleName()));
    }
}
