package by.babanin.todo.view.translat;

import java.util.Locale;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;

import by.babanin.todo.model.Status;
import by.babanin.todo.representation.ReportField;
import by.babanin.todo.view.exception.ViewException;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Translator {

    private static ResourceBundleMessageSource messageSource;

    public static void setMessageSource(ResourceBundleMessageSource messageSource) {
        if(Translator.messageSource != null) {
            throw new ViewException("messageSource is already exist");
        }
        Translator.messageSource = messageSource;
    }

    public static String toLocale(String code) {
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(code, null, locale);
    }

    public static String getStatusCaption(Status status) {
        return Translator.toLocale(String.format(TranslateCode.TODO_STATUS, status.name().toLowerCase()));
    }

    public static String getFieldCaption(ReportField field) {
        return Translator.toLocale(String.format(TranslateCode.FIELD_CAPTION, field.getDeclaringClass().getSimpleName(), field.getName()));
    }

    public static <T> String getComponentCaption(Class<T> componentClass) {
        return Translator.toLocale(String.format(TranslateCode.COMPONENT_CAPTION, componentClass.getSimpleName()));
    }

    public static <T> String getComponentPluralCaption(Class<T> componentClass) {
        return Translator.toLocale(String.format(TranslateCode.COMPONENT_PLURAL_CAPTION, componentClass.getSimpleName()));
    }
}
