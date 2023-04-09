package by.babanin.todo.preferences;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.PredicateUtils;
import org.apache.commons.collections4.map.PredicatedMap;
import org.apache.commons.lang3.StringUtils;

public final class PreferencesSupport {

    private final Map<String, PreferenceAware<?>> preferenceOwnerMap;

    public PreferencesSupport() {
        Predicate<Object> nullCheck = PredicateUtils.notNullPredicate();
        this.preferenceOwnerMap = PredicatedMap.predicatedMap(new LinkedHashMap<>(), nullCheck, nullCheck);
    }

    public void put(PreferenceAware<?> preferenceAware) {
        String key = preferenceAware.getKey();
        if(StringUtils.isBlank(key)) {
            throw new PreferenceException("Key is blank");
        }
        preferenceOwnerMap.put(key, preferenceAware);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void apply() {
        PreferencesStore preferencesStore = PreferencesStore.getInstance();
        preferenceOwnerMap.forEach(
                (key, preferenceAware) -> preferencesStore.get(key)
                        .ifPresentOrElse(
                                preference -> ((PreferenceAware) preferenceAware).apply(preference),
                                preferenceAware::resetToDefault
                        ));
    }

    public void save() {
        PreferencesStore preferencesStore = PreferencesStore.getInstance();
        preferenceOwnerMap.forEach((key, preferenceAware) -> preferencesStore.put(key, preferenceAware.createCurrentPreference()));
    }
}
