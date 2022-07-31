package by.babanin.todo.view.component.logger;

import javax.swing.Icon;

import by.babanin.todo.view.translat.TranslateCode;
import by.babanin.todo.view.translat.Translator;
import by.babanin.todo.view.util.IconResources;

public enum LogMessageType {
    ERROR(TranslateCode.ERRORS, "red_exclamation_triangle"),
    WARNING(TranslateCode.WARNINGS, "yellow_exclamation_triangle"),
    INFORMATION(TranslateCode.INFORMATION, "circled_information"),
    ;

    private final String captionCode;
    private final String iconName;

    LogMessageType(String captionCode, String iconName) {
        this.captionCode = captionCode;
        this.iconName = iconName;
    }

    @Override
    public String toString() {
        return Translator.toLocale(captionCode);
    }

    public String getCaption() {
        return Translator.toLocale(captionCode);
    }

    public Icon getIcon() {
        return IconResources.getIcon(iconName);
    }
}
