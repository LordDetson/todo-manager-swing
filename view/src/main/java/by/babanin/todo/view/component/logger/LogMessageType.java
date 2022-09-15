package by.babanin.todo.view.component.logger;

import javax.swing.Icon;

import by.babanin.todo.view.translat.TranslateCode;
import by.babanin.todo.view.translat.Translator;
import by.babanin.todo.image.IconResources;

public enum LogMessageType {
    ERROR(TranslateCode.ERRORS, "error"),
    WARNING(TranslateCode.WARNINGS, "warning"),
    INFORMATION(TranslateCode.INFORMATION, "information"),
    ;

    public static final int DEFAULT_ICON_SIZE = 16;

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
        return IconResources.getIcon(iconName, DEFAULT_ICON_SIZE);
    }
}
