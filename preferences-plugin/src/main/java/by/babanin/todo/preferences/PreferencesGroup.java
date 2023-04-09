package by.babanin.todo.preferences;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.PredicateUtils;
import org.apache.commons.collections4.map.PredicatedMap;
import org.apache.commons.lang3.StringUtils;

import lombok.Getter;

@Getter
public class PreferencesGroup implements Preference, Iterable<Map.Entry<String, Preference>> {

    private final Map<String, Preference> preferenceMap;

    public PreferencesGroup() {
        Predicate<Object> nullCheck = PredicateUtils.notNullPredicate();
        this.preferenceMap = PredicatedMap.predicatedMap(new HashMap<>(), nullCheck, nullCheck);
    }

    public void put(String key, Preference preference) {
        checkBlank(key);
        preferenceMap.put(key, preference);
    }

    public Optional<Preference> get(String key) {
        checkBlank(key);
        return Optional.ofNullable(preferenceMap.get(key));
    }

    private static void checkBlank(String key) {
        if(StringUtils.isBlank(key)) {
            throw new PreferenceException("Key is blank");
        }
    }

    @Override
    public Iterator<Entry<String, Preference>> iterator() {
        return preferenceMap.entrySet().iterator();
    }
}
