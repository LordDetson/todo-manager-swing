package by.babanin.todo.application.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.springframework.data.repository.CrudRepository;

import by.babanin.todo.application.exception.ApplicationException;
import by.babanin.todo.application.exception.TestException;
import by.babanin.todo.model.Indexable;
import by.babanin.todo.model.Persistent;

abstract class IndexableCrudServiceTest<E extends Persistent<I> & Indexable, I, S extends IndexableCrudService<E, I>>
        extends CrudServiceTest<E, I, S> {

    @Test
    void keepCorrectPositionAfterDeleteAll() {
        List<E> allList = getTestEntityHolder().getEntities();
        List<E> entitiesToDelete = List.of(
                allList.get(0),
                allList.get((allList.size() - 1) / 2),
                allList.get(allList.size() - 1)
        );

        getService().deleteAll(entitiesToDelete);

        List<E> expected = new ArrayList<>(allList);
        expected.removeAll(entitiesToDelete);
        checkPositions(getRepository(), expected);
    }

    @Test
    void keepCorrectPositionAfterDeleteAllById() {
        List<E> allList = getTestEntityHolder().getEntities();
        List<E> entitiesToDelete = List.of(
                allList.get(0),
                allList.get((allList.size() - 1) / 2),
                allList.get(allList.size() - 1)
        );
        Set<I> idsToDelete = entitiesToDelete.stream()
                .map(Persistent::getId)
                .collect(Collectors.toUnmodifiableSet());

        getService().deleteAllById(idsToDelete);

        List<E> expected = new ArrayList<>(allList);
        expected.removeAll(entitiesToDelete);
        checkPositions(getRepository(), expected);
    }

    @Test
    void keepCorrectPositionAfterDelete() {
        List<E> allList = getTestEntityHolder().getEntities();
        E entityToDelete = allList.get(0);

        getService().delete(entityToDelete);

        List<E> expected = new ArrayList<>(allList);
        expected.remove(entityToDelete);
        checkPositions(getRepository(), expected);
    }

    @Test
    void keepCorrectPositionAfterDeleteById() {
        List<E> allList = getTestEntityHolder().getEntities();
        E entityToDelete = allList.get(0);
        I idToDelete = entityToDelete.getId();

        getService().deleteById(idToDelete);

        List<E> expected = new ArrayList<>(allList);
        expected.remove(entityToDelete);
        checkPositions(getRepository(), expected);
    }

    @Test
    void insert() {
        E entityToInsert = getTestEntityHolder().getFakeEntities().get(0);

        getService().insert(0, entityToInsert);

        List<E> expected = new ArrayList<>(getTestEntityHolder().getEntities());
        expected.add(0, entityToInsert);
        checkPositions(getRepository(), expected);
    }

    @Test
    void insertWithNegativePosition() {
        E entityToInsert = getTestEntityHolder().getFakeEntities().get(0);

        S service = getService();
        assertThrows(ApplicationException.class, () -> service.insert(-1, entityToInsert));
    }

    @Test
    void insertWithPositionMoreThanEntitiesCount() {
        E entityToInsert = getTestEntityHolder().getFakeEntities().get(0);

        long position = getRepository().count() + 1;
        S service = getService();
        assertThrows(ApplicationException.class, () -> service.insert(position, entityToInsert));
    }

    @Test
    void insertWithPersistedEntity() {
        E entityToInsert = getTestEntityHolder().getEntities().get(0);

        S service = getService();
        assertThrows(ApplicationException.class, () -> service.insert(0, entityToInsert));
    }

    @Test
    void swap() {
        List<E> expected = new ArrayList<>(getTestEntityHolder().getEntities());
        long position1 = expected.get(0).getPosition();
        long position2 = expected.get(expected.size() - 1).getPosition();

        getService().swap(position1, position2);

        Collections.swap(expected, (int) position1, (int) position2);
        checkPositions(getRepository(), expected);
    }

    @Test
    void swapWithNegativePosition() {
        long position = getTestEntityHolder().getEntities().get(0).getPosition();

        S service = getService();
        assertThrows(ApplicationException.class, () -> service.swap(-1, position));
    }

    @Test
    void swapWithNotExistedPosition() {
        long position = getTestEntityHolder().getEntities().get(0).getPosition();
        long wrongPosition = getRepository().count();

        S service = getService();
        assertThrows(ApplicationException.class, () -> service.swap(wrongPosition, position));
    }

    @Test
    void getSubList() {
        long count = getRepository().count();
        long position1 = count - 2;
        long position2 = count - 1;

        List<E> actual = getService().getSubList(position1, position2);

        List<E> expected = getTestEntityHolder().getEntities().subList((int) position1, (int) position2);
        assertAll(
                () -> assertNotNull(actual),
                () -> assertEquals(expected.size(), actual.size()),
                () -> assertIterableEquals(expected, actual)
        );
    }

    @Test
    void getSubListWithInvertedRange() {
        long count = getRepository().count();
        long position1 = count - 2;
        long position2 = count - 1;

        S service = getService();
        assertThrows(ApplicationException.class, () -> service.getSubList(position2, position1));
    }

    @Test
    void getSubListWithToPositionsMoreThanCount() {
        long count = getRepository().count();
        long position1 = count - 1;
        long position2 = count + 1;

        S service = getService();
        assertThrows(ApplicationException.class, () -> service.getSubList(position1, position2));
    }

    @Test
    void getSubListWithFromPositionEqualCount() {
        long count = getRepository().count();

        S service = getService();
        assertThrows(ApplicationException.class, () -> service.getSubList(count, count));
    }

    @Test
    void getSubListWithToPositionEqualZero() {
        S service = getService();
        assertThrows(ApplicationException.class, () -> service.getSubList(0, 0));
    }

    @Test
    void getSubListWithNegativeRange() {
        long count = getRepository().count();
        long position1 = -1;
        long position2 = count - 1;

        S service = getService();
        assertThrows(ApplicationException.class, () -> service.getSubList(position1, position2));
    }

    public static <E extends Persistent<I> & Indexable, I> void checkPositions(CrudRepository<E, I> repository, List<E> expected) {
        assertEquals(expected.size(), repository.count());
        for(int i = 0; i < expected.size(); i++) {
            I id = expected.get(i).getId();
            Optional<E> entity = repository.findById(id);
            if(entity.isPresent()) {
                assertEquals(i, entity.get().getPosition());
            }
            else {
                throw new TestException("Entity should be exist");
            }
        }
    }
}