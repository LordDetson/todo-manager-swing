package by.babanin.todo.view.settings;

import java.util.List;

public interface SettingView<T extends Setting> {

    T getOriginal();

    T getAccumulator();

    SettingViewType getType();

    void addListener(SettingChangeListener<T> listener);

    List<SettingChangeListener<T>> getListeners();

    default void fireChange() {
        T accumulator = getAccumulator();
        getListeners().forEach(listener -> listener.settingChange(accumulator));
    }

    default boolean shouldApply() {
        return !getOriginal().equals(getAccumulator());
    }

    default T apply() {
        T original = getOriginal();
        original.update(getAccumulator());
        return original;
    }
}
