package by.babanin.todo.application.service;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import by.babanin.todo.model.Persistent;

public interface CrudService<E extends Persistent<I>, I> {

    List<E> deleteAll();

    List<E> deleteAll(Collection<E> entities);

    List<E> deleteAllById(Set<I> ids);

    E delete(E entity);

    E deleteById(I id);

    List<E> getAll();

    List<E> getAllById(Set<I> ids);

    E getById(I id);

    default boolean exist(E entity) {
        I id = entity.getId();
        return id != null && existById(id);
    }

    boolean existById(I id);

    long count();
}
