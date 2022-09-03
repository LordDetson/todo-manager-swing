package by.babanin.todo.application.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.jpa.repository.JpaRepository;

import by.babanin.todo.application.exception.ApplicationException;
import by.babanin.todo.application.holder.TestEntitiesHolder;
import by.babanin.todo.model.Persistent;

@DataJpaTest
abstract class CrudServiceTest<E extends Persistent<I>, I, S extends CrudService<E, I>> {

    @Autowired
    private S service;

    @Autowired
    private JpaRepository<E, I> repository;

    private TestEntitiesHolder<E, I> testEntitiesHolder;

    @BeforeEach
    void setupEach() {
        testEntitiesHolder = newTestEntitiesHolder();
        List<E> entities = repository.saveAll(testEntitiesHolder.getEntities());
        testEntitiesHolder.setEntities(entities);
    }

    protected abstract TestEntitiesHolder<E,I> newTestEntitiesHolder();

    @AfterEach
    void tearDownEach() {
        repository.deleteAll();
    }

    @Test
    void deleteAll() {
        List<E> allList = testEntitiesHolder.getEntities();

        List<E> result = service.deleteAll();

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(0L, repository.count()),
                () -> assertEquals(allList, result)
        );
    }

    @Test
    void deleteByEntities() {
        List<E> allList = testEntitiesHolder.getEntities();
        List<E> entitiesToDelete = List.of(
                allList.get(0),
                allList.get(allList.size() - 1)
        );
        Set<I> ids = entitiesToDelete.stream()
                .map(Persistent::getId)
                .collect(Collectors.toUnmodifiableSet());

        List<E> result = service.deleteAll(entitiesToDelete);

        assertAll(
                () -> assertNotNull(result),
                () -> assertTrue(repository.findAllById(ids).isEmpty()),
                () -> assertEquals(allList.size() - entitiesToDelete.size(), repository.count()),
                () -> assertEquals(entitiesToDelete.size(), result.size()),
                () -> assertIterableEquals(entitiesToDelete, result)
        );
    }

    @Test
    void deleteAllByNotExistedEntities() {
        List<E> notExistedEntities = testEntitiesHolder.getFakeEntities();

        assertAll(
                () -> assertThrows(EmptyResultDataAccessException.class, () -> service.deleteAll(notExistedEntities)),
                () -> assertEquals(testEntitiesHolder.getEntities().size(), repository.count())
        );
    }

    @Test
    void deleteAllById() {
        List<E> allList = testEntitiesHolder.getEntities();
        List<E> entitiesToDelete = List.of(
                allList.get(0),
                allList.get(allList.size() - 1)
        );
        Set<I> idsToDelete = entitiesToDelete.stream()
                .map(Persistent::getId)
                .collect(Collectors.toUnmodifiableSet());

        List<E> result = service.deleteAllById(idsToDelete);

        assertAll(
                () -> assertNotNull(result),
                () -> assertTrue(repository.findAllById(idsToDelete).isEmpty()),
                () -> assertEquals(allList.size() - idsToDelete.size(), repository.count()),
                () -> assertEquals(idsToDelete.size(), result.size()),
                () -> assertIterableEquals(entitiesToDelete, result)
        );
    }

    @Test
    void deleteAllByNotExistedId() {
        Set<I> notExistedIds = testEntitiesHolder.getFakeEntities().stream()
                .map(Persistent::getId)
                .collect(Collectors.toUnmodifiableSet());

        assertAll(
                () -> assertThrows(EmptyResultDataAccessException.class, () -> service.deleteAllById(notExistedIds)),
                () -> assertEquals(testEntitiesHolder.getEntities().size(), repository.count())
        );
    }

    @Test
    void deleteByEntity() {
        List<E> allList = testEntitiesHolder.getEntities();
        E entityToDelete = allList.get(0);
        I id = entityToDelete.getId();

        E result = service.delete(entityToDelete);

        assertAll(
                () -> assertNotNull(result),
                () -> assertFalse(repository.existsById(id)),
                () -> assertEquals(allList.size() - 1, repository.count()),
                () -> assertEquals(entityToDelete, result)
        );
    }

    @Test
    void deleteByNotExistedEntity() {
        E notExistedEntity = testEntitiesHolder.getFakeEntities().get(0);

        assertAll(
                () -> assertThrows(ApplicationException.class, () -> service.delete(notExistedEntity)),
                () -> assertEquals(testEntitiesHolder.getEntities().size(), repository.count())
        );
    }

    @Test
    void deleteById() {
        List<E> allList = testEntitiesHolder.getEntities();
        E entityToDelete = allList.get(0);
        I idToDelete = entityToDelete.getId();

        E result = service.deleteById(idToDelete);

        assertAll(
                () -> assertNotNull(result),
                () -> assertFalse(repository.existsById(idToDelete)),
                () -> assertEquals(allList.size() - 1, repository.count()),
                () -> assertEquals(entityToDelete, result)
        );
    }

    @Test
    void deleteByNotExistedId() {
        I notExistedId = testEntitiesHolder.getFakeEntities().get(0).getId();

        assertAll(
                () -> assertThrows(ApplicationException.class, () -> service.deleteById(notExistedId)),
                () -> assertEquals(testEntitiesHolder.getEntities().size(), repository.count())
        );
    }

    @Test
    void getAll() {
        List<E> expected = testEntitiesHolder.getEntities();

        List<E> actual = service.getAll();

        assertAll(
                () -> assertNotNull(actual),
                () -> assertEquals(expected.size(), actual.size()),
                () -> assertIterableEquals(expected, actual)
        );
    }

    @Test
    void getAllById() {
        List<E> entities = testEntitiesHolder.getEntities();
        List<E> expected = List.of(
                entities.get(0),
                entities.get(entities.size() - 1)
        );
        Set<I> ids = expected.stream()
                .map(Persistent::getId)
                .collect(Collectors.toUnmodifiableSet());

        List<E> actual = service.getAllById(ids);

        assertAll(
                () -> assertNotNull(actual),
                () -> assertEquals(expected.size(), actual.size()),
                () -> assertIterableEquals(expected, actual)
        );
    }

    @Test
    void getEmptyByOneNotExistedId() {
        I notExistedId = testEntitiesHolder.getFakeEntities().get(0).getId();

        List<E> actual = service.getAllById(Collections.singleton(notExistedId));

        assertAll(
                () -> assertNotNull(actual),
                () -> assertTrue(actual.isEmpty())
        );
    }

    @Test
    void getEmptyByNotExistedIds() {
        Set<I> notExistedIds = testEntitiesHolder.getFakeEntities().stream()
                .map(Persistent::getId)
                .collect(Collectors.toUnmodifiableSet());

        List<E> actual = service.getAllById(notExistedIds);

        assertAll(
                () -> assertNotNull(actual),
                () -> assertTrue(actual.isEmpty())
        );
    }

    @Test
    void getAllOnlyExistById() {
        List<E> notExistedEntities = testEntitiesHolder.getFakeEntities();
        Set<I> notExistedIds = notExistedEntities.stream()
                .map(Persistent::getId)
                .collect(Collectors.toUnmodifiableSet());
        List<E> entities = testEntitiesHolder.getEntities();
        List<E> expected = List.of(
                entities.get(0),
                entities.get(entities.size() - 1)
        );
        Set<I> existedIds = expected.stream()
                .map(Persistent::getId)
                .collect(Collectors.toUnmodifiableSet());
        Set<I> ids = new HashSet<>();
        ids.addAll(notExistedIds);
        ids.addAll(existedIds);

        List<E> actual = service.getAllById(ids);

        assertAll(
                () -> assertNotNull(actual),
                () -> assertEquals(expected.size(), actual.size()),
                () -> assertIterableEquals(expected, actual)
        );
    }

    @Test
    void getById() {
        E expected = testEntitiesHolder.getEntities().get(0);

        E actual = service.getById(expected.getId());

        assertAll(
                () -> assertNotNull(actual),
                () -> assertEquals(expected, actual)
        );
    }

    @Test
    void notGetById() {
        I notExistedId = testEntitiesHolder.getFakeEntities().get(0).getId();

        assertThrows(ApplicationException.class, () -> service.getById(notExistedId));
    }

    @Test
    void existById() {
        E entity = testEntitiesHolder.getEntities().get(0);

        boolean actual = service.existById(entity.getId());

        assertTrue(actual);
    }

    @Test
    void notExistById() {
        I notExistedId = testEntitiesHolder.getFakeEntities().get(0).getId();

        boolean actual = service.existById(notExistedId);

        assertFalse(actual);
    }

    S getService() {
        return service;
    }

    JpaRepository<E, I> getRepository() {
        return repository;
    }

    public TestEntitiesHolder<E, I> getTestEntityHolder() {
        return testEntitiesHolder;
    }
}