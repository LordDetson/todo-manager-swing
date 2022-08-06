package by.babanin.todo.application.service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.repository.JpaRepository;

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
        Set<I> ids = entities.stream()
                .map(E::getId)
                .collect(Collectors.toUnmodifiableSet());
        return deleteAllById(ids);
    }

    @Transactional
    @Override
    public List<E> deleteAllById(Set<I> ids) {
        List<E> prioritiesToDelete = getAllById(ids);
        if(prioritiesToDelete.isEmpty()) {
            throw new ApplicationException("Doesn't have such ids");
        }
        updateReferencesBeforeDelete(prioritiesToDelete);
        repository.deleteAllById(ids);
        return prioritiesToDelete;
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
        return deleteAllById(Collections.singleton(id)).get(0);
    }

    @Transactional
    @Override
    public List<E> getAll() {
        return Collections.unmodifiableList(repository.findAll(Sort.by(Direction.ASC, Fields.position)));
    }

    @Transactional
    @Override
    public List<E> getAllById(Set<I> ids) {
        return repository.findAllById(ids);
    }

    @Transactional
    @Override
    public E getById(I id) {
        if(!existById(id)) {
            throw new ApplicationException("No such component by " + id + " id");
        }
        return repository.getReferenceById(id);
    }

    @Transactional
    @Override
    public boolean existById(I id) {
        return repository.existsById(id);
    }

    @Transactional
    @Override
    public long count() {
        return repository.count();
    }
}
