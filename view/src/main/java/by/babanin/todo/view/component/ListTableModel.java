package by.babanin.todo.view.component;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

public interface ListTableModel<C> {

    void addAll(Collection<C> components);

    default void add(C component) {
        addAll(Collections.singletonList(component));
    }

    void add(int index, C component);

    void set(int index, C component);

    C get(int row);

    default List<C> get(int[] indices) {
        return Arrays.stream(indices)
                .mapToObj(this::get)
                .toList();
    }

    List<C> getAll();

    int indexOf(C component);

    IntStream indexOf(Collection<C> components);

    void remove(Collection<C> components);

    void clear();

    int size();
}
