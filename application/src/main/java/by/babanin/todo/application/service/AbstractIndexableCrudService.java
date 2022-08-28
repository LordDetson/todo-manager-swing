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
        minPosition.ifPresent(this::restorePositions);
        return prioritiesToDelete;
    }

    @Override
    public E deleteById(I id) {
        E priorityToDelete = getById(id);
        long minPosition = priorityToDelete.getPosition();
        priorityToDelete = super.deleteById(id);
        restorePositions(minPosition);
        return priorityToDelete;
    }

    private void restorePositions(long minPosition) {
        List<E> greaterPositionEntities = getRepository().findByPositionGreaterThanOrderByPositionAsc(minPosition);
        for(int i = 0; i < greaterPositionEntities.size(); i++) {
            greaterPositionEntities.get(i).setPosition(i + minPosition);
        }
        getRepository().saveAll(greaterPositionEntities);
    }

    @Transactional
    @Override
    public E insert(long position, E entity) {
        if(exist(entity)) {
            throw new ApplicationException("The entity is already exist");
        }
        List<E> subList = getSubList(position, count());
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

    @Transactional
    @Override
    public List<E> getSubList(long from, long to) {
        if(from < 0) {
            throw new ApplicationException("The 'from' position can't be less than 0");
        }
        if(to <= 0) {
            throw new ApplicationException("The 'to' position can't be less than or equal 0");
        }
        long count = getRepository().count();
        if(from >= count) {
            throw new ApplicationException("The 'from' position can't be more than or equal " + count);
        }
        if(to > count) {
            throw new ApplicationException("The 'to' position can't be more than " + count);
        }
        if(from >= to) {
            throw new ApplicationException("The 'from' position should be less then the 'to' position");
        }
        return getRepository().findByPositionGreaterThanEqualAndPositionLessThan(from, to);
    }
}
