package by.babanin.todo.view.settings;

@FunctionalInterface
public interface SettingChangeListener<T extends Setting> {

    void settingChange(T setting);
}
