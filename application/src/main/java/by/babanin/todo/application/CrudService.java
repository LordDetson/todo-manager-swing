package by.babanin.todo.application;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import by.babanin.todo.model.Entity;

public interface CrudService<E extends Entity<I>, I> {

    E save(E entity);

    void deleteAll(Collection<E> entities);

    void deleteAllById(Set<I> ids);

    void delete(E entity);

    void deleteById(I id);

    List<E> getAll();

    E getById(I id);

    default boolean exist(E entity) {
        return existById(entity.getId());
    }

    boolean existById(I id);
}
