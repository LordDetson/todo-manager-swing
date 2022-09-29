package by.babanin.todo.view;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import by.babanin.todo.view.component.View;
import by.babanin.todo.view.exception.ViewException;

public class LazyViewContainer implements Loadable {

    private final List<LazyViewListener> lazyViewListeners = new ArrayList<>();

    private final Supplier<View> viewFactory;
    private View initializedView;

    public LazyViewContainer(Supplier<View> viewFactory) {
        this.viewFactory = viewFactory;
    }

    public View get() {
        if(!isInitialized()) {
            initializedView = viewFactory.get();
            if(initializedView == null) {
                throw new ViewException("View isn't initialized");
            }
            lazyViewListeners.forEach(listener -> listener.initialized(initializedView));
        }
        return initializedView;
    }

    public boolean isInitialized() {
        return initializedView != null;
    }

    public void doIfInitialized(Consumer<View> consumer) {
        if(isInitialized()) {
            consumer.accept(initializedView);
        }
    }

    public void addListener(LazyViewListener listener) {
        lazyViewListeners.add(listener);
    }

    public void removeListener(LazyViewListener listener) {
        lazyViewListeners.remove(listener);
    }

    @Override
    public void load() {
        doIfInitialized(View::load);
    }

    @Override
    public void clear() {
        doIfInitialized(View::clear);
    }
}
