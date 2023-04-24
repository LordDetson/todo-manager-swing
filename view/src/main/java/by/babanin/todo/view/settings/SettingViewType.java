package by.babanin.todo.view.settings;

import java.util.Arrays;
import java.util.function.BiFunction;

import by.babanin.todo.view.exception.ViewException;
import by.babanin.todo.view.translat.Translator;

public enum SettingViewType {
    TABLE_COLUMN_ADJUSTMENT("tableColumnAdjustment",
            (settings, type) -> new TableColumnAdjustmentView(settings.getTableColumnAdjustment(), type)),
    ;

    private final String id;
    private final BiFunction<Settings, SettingViewType, SettingView<?>> viewFactory;

    SettingViewType(String id, BiFunction<Settings, SettingViewType, SettingView<?>> viewFactory) {
        this.id = id;
        this.viewFactory = viewFactory;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return Translator.getSettingViewTitle(this);
    }

    public String getDescription() {
        return Translator.getSettingViewDescription(this);
    }

    @SuppressWarnings("unchecked")
    public SettingView<Setting> createView(Settings settings) {
        SettingView<?> view = viewFactory.apply(settings, this);
        ((java.awt.Component) view).setVisible(false);
        return (SettingView<Setting>) view;
    }

    public static SettingViewType get(String id) {
        return Arrays.stream(SettingViewType.values())
                .filter(type -> type.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new ViewException(id + " type not found"));
    }
}
