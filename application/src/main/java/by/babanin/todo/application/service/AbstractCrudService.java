package by.babanin.todo.application.service;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import by.babanin.todo.model.Persistent;

public abstract class AbstractCrudService<E extends Persistent<I>, I> implements CrudService<E, I>{

    @Override
    public void deleteAll(Collection<E> entities) {
        Set<I> ids = entities.stream()
                .map(E::getId)
                .collect(Collectors.toUnmodifiableSet());
        deleteAllById(ids);
    }

    @Override
    public void delete(E entity) {
        deleteById(entity.getId());
    }

    @Override
    public void deleteById(I id) {
        deleteAllById(Collections.singleton(id));
    }
}
