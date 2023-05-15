package by.babanin.todo.ui.translat;

import by.babanin.ext.message.Translator;
import by.babanin.todo.model.Status;
import lombok.experimental.UtilityClass;

@UtilityClass
public class AppTranslator {

    public static String getStatusCaption(Status status) {
        return Translator.toLocale(String.format(AppTranslateCode.TODO_STATUS, status.name().toLowerCase()));
    }
}
