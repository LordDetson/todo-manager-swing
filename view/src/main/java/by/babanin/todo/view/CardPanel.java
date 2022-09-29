package by.babanin.todo.view;

import java.awt.CardLayout;
import java.awt.Component;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

import javax.swing.JPanel;

import org.apache.commons.lang3.StringUtils;

import by.babanin.todo.view.component.View;

public class CardPanel extends JPanel implements View {

    private final Map<String, LazyViewContainer> lazyViewContainerMap = new LinkedHashMap<>();
    private final List<ViewChangedListener> listeners = new ArrayList<>();

    private CardLayout cardLayout;
    private String visibleViewName;
    private View visibleView;

    public CardPanel() {
        initialize();
    }

    @Override
    public void createUiComponents() {
        cardLayout = new CardLayout();
    }

    @Override
    public void addListeners() {
        // do nothing
    }

    @Override
    public void placeComponents() {
        setLayout(cardLayout);
    }

    @Override
    public void load() {
        if(visibleView != null) {
            visibleView.load();
        }
    }

    @Override
    public void clear() {
        if(visibleView != null) {
            visibleView.clear();
        }
    }

    public void addView(String viewName, Supplier<View> viewFactory) {
        if(StringUtils.isNotBlank(viewName) && viewFactory != null) {
            LazyViewContainer lazyViewContainer = new LazyViewContainer(viewFactory);
            lazyViewContainerMap.put(viewName, lazyViewContainer);
        }
    }

    public void showView(String viewName) {
        if(!Objects.equals(viewName, visibleViewName)) {
            LazyViewContainer lazyViewContainer = lazyViewContainerMap.get(viewName);
            if(lazyViewContainer != null) {
                clear();
                visibleViewName = viewName;
                boolean wasNotInitialized = !lazyViewContainer.isInitialized();
                visibleView = lazyViewContainer.get();
                if(wasNotInitialized) {
                    add((Component) visibleView, visibleViewName);
                }
                cardLayout.show(this, viewName);
                listeners.forEach(listener -> listener.viewChanged(visibleViewName, visibleView));
                load();
            }
        }
    }

    public String getVisibleViewName() {
        return visibleViewName;
    }

    public View getVisibleView() {
        return visibleView;
    }

    public void addViewChangedListener(ViewChangedListener listener) {
        listeners.add(listener);
    }

    public void removeViewChangedListener(ViewChangedListener listener) {
        listeners.remove(listener);
    }
}
