package by.babanin.todo.application.service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import by.babanin.todo.application.exception.ApplicationException;
import by.babanin.todo.model.Persistent;
import by.babanin.todo.model.Priority.Fields;

public abstract class AbstractCrudService<E extends Persistent<I>, I> implements CrudService<E, I> {

    private final JpaRepository<E, I> repository;

    protected AbstractCrudService(JpaRepository<E, I> repository) {
        this.repository = repository;
    }

    protected JpaRepository<E, I> getRepository() {
        return repository;
    }

    @Transactional
    @Override
    public List<E> deleteAll() {
        List<E> entities = getAll();
        repository.deleteAll();
        return entities;
    }

    @Transactional
    @Override
    public List<E> deleteAll(Collection<E> entities) {
        if(entities == null || entities.isEmpty()) {
            throw new ApplicationException("Entities list is empty");
        }
        Set<I> ids = entities.stream()
                .map(E::getId)
                .collect(Collectors.toUnmodifiableSet());
        return deleteAllById(ids);
    }

    @Transactional
    @Override
    public List<E> deleteAllById(Set<I> ids) {
        if(ids == null || ids.isEmpty()) {
            throw new ApplicationException("ID list is empty");
        }
        List<E> entitiesToDelete = getAllById(ids);
        if(entitiesToDelete.isEmpty()) {
            throw new ApplicationException("No such component by ids");
        }
        updateReferencesBeforeDelete(entitiesToDelete);
        repository.deleteAllById(ids);
        return entitiesToDelete;
    }

    protected void updateReferencesBeforeDelete(Collection<E> entitiesToDelete) {
        // do nothing by default
    }

    @Transactional
    @Override
    public E delete(E entity) {
        return deleteById(entity.getId());
    }

    @Transactional
    @Override
    public E deleteById(I id) {
        E entityToDelete = getById(id);
        updateReferencesBeforeDelete(Collections.singleton(entityToDelete));
        repository.deleteById(id);
        return entityToDelete;
    }

    @Transactional(readOnly = true)
    @Override
    public List<E> getAll() {
        return repository.findAll(Sort.by(Direction.ASC, Fields.position));
    }

    @Transactional(readOnly = true)
    @Override
    public List<E> getAllById(Set<I> ids) {
        return repository.findAllById(ids);
    }

    @Transactional(readOnly = true)
    @Override
    public E getById(I id) {
        return repository.findById(id)
                .orElseThrow(() -> new ApplicationException("No such component by " + id + " id"));
    }

    @Transactional(readOnly = true)
    @Override
    public boolean existById(I id) {
        return repository.existsById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public long count() {
        return repository.count();
    }
}
