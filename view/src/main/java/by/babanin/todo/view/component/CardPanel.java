package by.babanin.todo.view.component;

import java.awt.CardLayout;
import java.awt.Component;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import javax.swing.JPanel;

import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.PredicateUtils;
import org.apache.commons.collections4.map.PredicatedMap;
import org.apache.commons.lang3.StringUtils;

import by.babanin.todo.preferences.PreferenceException;
import by.babanin.todo.view.exception.ViewException;

public class CardPanel<T> extends JPanel {

    private final Map<String, LazyContainer<T>> lazyContainerMap;

    public CardPanel() {
        super(new CardLayout());
        Predicate<Object> nullCheck = PredicateUtils.notNullPredicate();
        this.lazyContainerMap = PredicatedMap.predicatedMap(new HashMap<>(), nullCheck, nullCheck);
    }

    @Override
    public CardLayout getLayout() {
        return (CardLayout) super.getLayout();
    }

    public LazyContainer<T> put(String key, Supplier<T> factory) {
        checkBlank(key);
        if(factory == null) {
            throw new ViewException("Factory is null");
        }
        LazyContainer<T> lazyContainer = new LazyContainer<>(factory);
        lazyContainerMap.put(key, lazyContainer);
        return lazyContainer;
    }

    public void showCard(String key) {
        checkBlank(key);
        T view = get(key);
        Component component = (Component) view;
        component.setVisible(false);
        add(component, key);
        getLayout().show(this, key);
    }

    public T get(String key) {
        return Optional.ofNullable(lazyContainerMap.get(key))
                .orElseThrow(() -> new ViewException(key + " keys not found. Use the method to add."))
                .get();
    }

    public List<T> getAllInitialized() {
        return lazyContainerMap.values().stream()
                .filter(LazyContainer::isInitialized)
                .map(LazyContainer::get)
                .toList();
    }

    public boolean contains(String key) {
        return lazyContainerMap.containsKey(key);
    }

    private static void checkBlank(String key) {
        if(StringUtils.isBlank(key)) {
            throw new PreferenceException("Key is blank");
        }
    }
}
