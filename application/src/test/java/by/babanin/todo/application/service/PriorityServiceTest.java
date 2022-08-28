package by.babanin.todo.application.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import by.babanin.todo.application.exception.ApplicationException;
import by.babanin.todo.application.holder.PrioritiesHolder;
import by.babanin.todo.application.holder.TestEntitiesHolder;
import by.babanin.todo.model.Priority;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class PriorityServiceTest extends IndexableCrudServiceTest<Priority, Long, PriorityService> {

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
        Priority priority = getService().create(priorityName, position);

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
        assertThrows(ApplicationException.class, () -> service.create(name, position));
    }

    @Test
    void createWithPositionMoreThanEntitiesCount() {
        String name = getTestEntityHolder().getFakeEntities().get(0).getName();
        long position = getRepository().count() + 1;

        PriorityService service = getService();
        assertThrows(ApplicationException.class, () -> service.create(name, position));
    }

    @Test
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
    void renameWithExistedName() {
        Priority priority = getTestEntityHolder().getEntities().get(0);
        Long id = priority.getId();
        String name = getTestEntityHolder().getEntities().get(0).getName();

        PriorityService service = getService();
        assertThrows(ApplicationException.class, () -> service.rename(id, name));
    }

    @Test
    void renameWithForbiddenSymbol() {
        Priority priority = getTestEntityHolder().getEntities().get(0);
        Long id = priority.getId();
        String name = priority.getName();
        PriorityService service = getService();
        List<String> names = service.getForbiddenSymbolsForName().stream()
                .map(forbiddenSymbol -> name + forbiddenSymbol)
                .toList();

        names.forEach(newName -> assertThrows(ApplicationException.class, () -> service.rename(id, newName)));
    }

    @Test
    void renameWithEmptyName() {
        Long id = getTestEntityHolder().getEntities().get(0).getId();
        List<String> names = new ArrayList<>();
        names.add(null);
        names.add("");
        names.add(" ");

        PriorityService service = getService();
        names.forEach(name -> assertThrows(ApplicationException.class, () -> service.rename(id, name)));
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
}