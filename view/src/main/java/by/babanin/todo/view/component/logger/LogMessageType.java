package by.babanin.todo.view.component.logger;

import javax.swing.Icon;

import by.babanin.ext.message.Translator;
import by.babanin.todo.image.IconResources;
import by.babanin.todo.view.translat.AppTranslateCode;

public enum LogMessageType {
    ERROR(AppTranslateCode.ERRORS, "error"),
    WARNING(AppTranslateCode.WARNINGS, "warning"),
    INFORMATION(AppTranslateCode.INFORMATION, "information"),
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
