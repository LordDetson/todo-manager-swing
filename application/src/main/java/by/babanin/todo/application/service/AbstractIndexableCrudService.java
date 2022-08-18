package by.babanin.todo.application.service;

import java.util.List;
import java.util.OptionalLong;
import java.util.Set;

import javax.transaction.Transactional;

import by.babanin.todo.application.exception.ApplicationException;
import by.babanin.todo.application.repository.IndexableRepository;
import by.babanin.todo.model.Indexable;
import by.babanin.todo.model.Persistent;

public abstract class AbstractIndexableCrudService<E extends Persistent<I> & Indexable, I>
        extends AbstractCrudService<E, I>
        implements IndexableCrudService<E, I> {

    protected AbstractIndexableCrudService(IndexableRepository<E, I> repository) {
        super(repository);
    }

    @Override
    protected IndexableRepository<E, I> getRepository() {
        return (IndexableRepository<E, I>) super.getRepository();
    }

    @Transactional
    @Override
    public List<E> deleteAllById(Set<I> ids) {
        List<E> prioritiesToDelete = getAllById(ids);
        OptionalLong minPosition = prioritiesToDelete.stream()
                .mapToLong(E::getPosition)
                .min();
        prioritiesToDelete = super.deleteAllById(ids);
        if(minPosition.isPresent()) {
            List<E> greaterPositionEntities = getRepository().findByPositionGreaterThanOrderByPositionAsc(minPosition.getAsLong());
            for(int i = 0; i < greaterPositionEntities.size(); i++) {
                greaterPositionEntities.get(i).setPosition(i + minPosition.getAsLong());
            }
            getRepository().saveAll(greaterPositionEntities);
        }
        return prioritiesToDelete;
    }

    @Transactional
    @Override
    public E insert(long position, E entity) {
        List<E> subList = getAll().subList((int) position, (int) count());
        entity.setPosition(position);
        entity = getRepository().save(entity);
        subList.forEach(item -> item.setPosition(item.getPosition() + 1));
        getRepository().saveAll(subList);
        return getById(entity.getId());
    }

    @Transactional
    @Override
    public void swap(long position1, long position2) {
        IndexableRepository<E, I> repository = getRepository();
        E entity1 = repository.findByPosition(position1)
                .orElseThrow(() -> new ApplicationException("Object not found by position " + position1));
        E entity2 = repository.findByPosition(position2)
                .orElseThrow(() -> new ApplicationException("Object not found by position " + position2));
        entity1.setPosition(position2);
        entity2.setPosition(position1);
        repository.saveAll(List.of(entity1, entity2));
    }
}
