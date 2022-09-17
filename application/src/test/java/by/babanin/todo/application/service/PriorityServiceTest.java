package by.babanin.todo.application.service;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import by.babanin.todo.application.exception.ApplicationException;
import by.babanin.todo.application.holder.PrioritiesHolder;
import by.babanin.todo.application.holder.TestEntitiesHolder;
import by.babanin.todo.model.Priority;
import by.babanin.todo.model.Todo;

class PriorityServiceTest extends IndexableCrudServiceTest<Priority, Long, PriorityService> {

    @Autowired
    private TodoService todoService;

    @Override
    protected TestEntitiesHolder<Priority, Long> newTestEntitiesHolder() {
        return new PrioritiesHolder();
    }

    @Test
    void create() {
        String priorityName = getTestEntityHolder().getFakeEntities().get(0).getName();
        Priority priority = getService().create(priorityName);

        assertAll(
                () -> assertNotNull(priority),
                () -> assertNotNull(priority.getId()),
                () -> assertTrue(getRepository().existsById(priority.getId())),
                () -> assertEquals(priorityName, priority.getName()),
                () -> assertEquals(getRepository().count() - 1, priority.getPosition())
        );
    }

    @Test
    void createWithPosition() {
        String priorityName = getTestEntityHolder().getFakeEntities().get(0).getName();
        List<Priority> entities = new ArrayList<>(getTestEntityHolder().getEntities());
        long position = entities.get(entities.size() - 3).getPosition();
        Priority priority = getService().create(position, priorityName);

        entities.add((int) position, priority);
        assertAll(
                () -> assertNotNull(priority),
                () -> assertNotNull(priority.getId()),
                () -> assertTrue(getRepository().existsById(priority.getId())),
                () -> assertEquals(priorityName, priority.getName()),
                () -> assertEquals(position, priority.getPosition()),
                () -> checkPositions(getRepository(), entities)
        );
    }

    @Test
    void createWithExistedName() {
        String name = getTestEntityHolder().getEntities().get(0).getName();

        PriorityService service = getService();
        assertThrows(ApplicationException.class, () -> service.create(name));
    }

    @Test
    void createWithForbiddenSymbol() {
        List<String> names = new ArrayList<>();
        names.add("test\nname");
        names.add("test+name");
        names.add("test1");
        names.add("test?");
        names.add("<test>");
        names.add("[test]");

        PriorityService service = getService();
        names.addAll(service.getForbiddenSymbolsForName());
        names.forEach(name -> assertThrows(ApplicationException.class, () -> service.create(name)));
    }

    @Test
    void createWithEmptyName() {
        List<String> names = new ArrayList<>();
        names.add(null);
        names.add("");
        names.add(" ");

        PriorityService service = getService();
        names.forEach(name -> assertThrows(ApplicationException.class, () -> service.create(name)));
    }

    @Test
    void createWithNegativePosition() {
        String name = getTestEntityHolder().getFakeEntities().get(0).getName();
        long position = -1;

        PriorityService service = getService();
        assertThrows(ApplicationException.class, () -> service.create(position, name));
    }

    @Test
    void createWithPositionMoreThanEntitiesCount() {
        String name = getTestEntityHolder().getFakeEntities().get(0).getName();
        long position = getRepository().count() + 1;

        PriorityService service = getService();
        assertThrows(ApplicationException.class, () -> service.create(position, name));
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    void rename() {
        String newName = getTestEntityHolder().getFakeEntities().get(0).getName();
        List<Priority> entities = getTestEntityHolder().getEntities();
        Long id = getTestEntityHolder().getEntities().get(0).getId();

        Priority priority = getService().rename(id, newName);

        assertAll(
                () -> assertNotNull(priority),
                () -> assertNotNull(priority.getId()),
                () -> assertEquals(id, priority.getId()),
                () -> assertTrue(getRepository().existsById(priority.getId())),
                () -> assertEquals(newName, priority.getName()),
                () -> checkPositions(getRepository(), entities)
        );
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    void renameWithExistedName() {
        List<Priority> entities = getTestEntityHolder().getEntities();
        Priority priority = entities.get(0);
        Long id = priority.getId();
        String name = entities.get(1).getName();

        PriorityService service = getService();
        assertAll(
                () -> assertThrows(ApplicationException.class, () -> service.rename(id, name)),
                () -> assertEquals(priority.getName(), service.getById(id).getName())
        );
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    void renameWithSameName() {
        List<Priority> entities = getTestEntityHolder().getEntities();
        Priority priority = entities.get(0);
        Long id = priority.getId();

        PriorityService service = getService();
        Priority result = service.rename(id, priority.getName());
        assertEquals(priority.getName(), result.getName());
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    void renameWithForbiddenSymbol() {
        Priority priority = getTestEntityHolder().getEntities().get(0);
        Long id = priority.getId();
        String name = priority.getName();
        PriorityService service = getService();
        List<String> names = service.getForbiddenSymbolsForName().stream()
                .map(forbiddenSymbol -> name + forbiddenSymbol)
                .toList();

        names.forEach(newName -> assertAll(
                () -> assertThrows(ApplicationException.class, () -> service.rename(id, newName)),
                () -> assertEquals(priority.getName(), service.getById(id).getName())
        ));
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    void renameWithEmptyName() {
        Priority priority = getTestEntityHolder().getEntities().get(0);
        Long id = priority.getId();
        List<String> names = new ArrayList<>();
        names.add(null);
        names.add("");
        names.add(" ");

        PriorityService service = getService();
        names.forEach(name -> assertAll(
                () -> assertThrows(ApplicationException.class, () -> service.rename(id, name)),
                () -> assertEquals(priority.getName(), service.getById(id).getName())
        ));
    }

    @Test
    void findByName() {
        Priority priority = getTestEntityHolder().getEntities().get(0);
        String name = priority.getName();

        Optional<Priority> foundPriority = getService().findByName(name);

        assertAll(
                () -> assertNotNull(foundPriority),
                () -> assertTrue(foundPriority.isPresent()),
                () -> assertEquals(priority, foundPriority.orElse(null))
        );
    }

    @Test
    void findByNotExistedName() {
        String name = getTestEntityHolder().getFakeEntities().get(0).getName();

        Optional<Priority> foundPriority = getService().findByName(name);

        assertAll(
                () -> assertNotNull(foundPriority),
                () -> assertTrue(foundPriority.isEmpty())
        );
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    void deleteWithLinkedTodos() {
        Priority priority = getTestEntityHolder().getEntities().get(0);
        Todo todo = todoService.create("Test1", "Desciption", priority, LocalDate.now());

        getService().delete(priority);

        assertNull(todoService.getById(todo.getId()).getPriority());
        todoService.deleteAll();
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    void deleteAllWithLinkedTodos() {
        Priority priority = getTestEntityHolder().getEntities().get(0);
        Todo todo = todoService.create("Test1", "Desciption", priority, LocalDate.now());

        getService().deleteAll();

        assertNull(todoService.getById(todo.getId()).getPriority());
        todoService.deleteAll();
    }
}