package by.babanin.todo.application.service;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import by.babanin.todo.model.Persistent;

public interface CrudService<E extends Persistent<I>, I> {

    E save(E entity);

    void deleteAll();

    void deleteAll(Collection<E> entities);

    void deleteAllById(Set<I> ids);

    void delete(E entity);

    void deleteById(I id);

    List<E> getAll();

    List<E> getAllById(Set<I> ids);

    E getById(I id);

    default boolean exist(E entity) {
        return existById(entity.getId());
    }

    boolean existById(I id);
}
