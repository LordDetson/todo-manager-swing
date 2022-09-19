package by.babanin.todo.application.service;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import by.babanin.todo.application.exception.ApplicationException;
import by.babanin.todo.application.holder.TestEntitiesHolder;
import by.babanin.todo.application.holder.TodosHolder;
import by.babanin.todo.application.status.StatusWorkflow;
import by.babanin.todo.model.Priority;
import by.babanin.todo.model.Status;
import by.babanin.todo.model.Todo;


class TodoServiceTest extends IndexableCrudServiceTest<Todo, Long, TodoService> {

    @Autowired
    private PriorityService priorityService;

    @Override
    protected TestEntitiesHolder<Todo, Long> newTestEntitiesHolder() {
        return new TodosHolder();
    }

    @Test
    void create() {
        Priority priority = priorityService.create("High");
        Todo fakeTodo = getTestEntityHolder().getFakeEntities().get(0);

        Todo todo = getService().create(
                fakeTodo.getTitle(),
                fakeTodo.getDescription(),
                priority,
                fakeTodo.getPlannedDate()
        );

        List<Todo> todos = new ArrayList<>(getTestEntityHolder().getEntities());
        todos.add(0, todo);
        assertAll(
                () -> assertNotNull(todo),
                () -> assertNotNull(todo.getId()),
                () -> assertTrue(getRepository().existsById(todo.getId())),
                () -> assertEquals(fakeTodo.getTitle(), todo.getTitle()),
                () -> assertEquals(fakeTodo.getDescription(), todo.getDescription()),
                () -> assertEquals(priority, todo.getPriority()),
                () -> assertEquals(Status.OPEN, todo.getStatus()),
                () -> assertEquals(LocalDate.now(), todo.getCreationDate()),
                () -> assertEquals(fakeTodo.getPlannedDate(), todo.getPlannedDate()),
                () -> assertNull(todo.getCompletionDate()),
                () -> checkPositions(getRepository(), todos)
        );
        priorityService.deleteAll();
    }

    @Test
    void createWithPosition() {
        Todo fakeTodo = getTestEntityHolder().getFakeEntities().get(0);
        long position = getRepository().count() - 1;

        Todo todo = getService().create(
                position,
                fakeTodo.getTitle(),
                fakeTodo.getDescription(),
                fakeTodo.getPriority(),
                fakeTodo.getPlannedDate()
        );

        List<Todo> todos = new ArrayList<>(getTestEntityHolder().getEntities());
        todos.add(todos.size() - 1, todo);
        assertAll(
                () -> assertNotNull(todo),
                () -> assertNotNull(todo.getId()),
                () -> assertTrue(getRepository().existsById(todo.getId())),
                () -> assertEquals(fakeTodo.getTitle(), todo.getTitle()),
                () -> assertEquals(fakeTodo.getDescription(), todo.getDescription()),
                () -> assertEquals(fakeTodo.getPriority(), todo.getPriority()),
                () -> assertEquals(fakeTodo.getPlannedDate(), todo.getPlannedDate()),
                () -> assertEquals(Status.OPEN, todo.getStatus()),
                () -> assertEquals(LocalDate.now(), todo.getCreationDate()),
                () -> checkPositions(getRepository(), todos)
        );
    }

    @Test
    void createWithEmptyTitle() {
        Todo fakeTodo = getTestEntityHolder().getFakeEntities().get(0);
        List<String> titles = new ArrayList<>();
        titles.add(null);
        titles.add("");
        titles.add("  ");
        String description = fakeTodo.getDescription();
        Priority priority = fakeTodo.getPriority();
        LocalDate plannedDate = fakeTodo.getPlannedDate();

        TodoService service = getService();
        titles.forEach(title -> assertAll(
                () -> assertThrows(ApplicationException.class,
                        () -> service.create(
                                title,
                                description,
                                priority,
                                plannedDate
                        )),
                () -> assertEquals(getTestEntityHolder().getEntities().size(), getRepository().count())
        ));
    }

    @Test
    void createWithEmptyDescription() {
        Todo fakeTodo = getTestEntityHolder().getFakeEntities().get(0);
        List<String> descriptions = new ArrayList<>();
        descriptions.add(null);
        descriptions.add("");
        descriptions.add("  ");

        List<Todo> result = new ArrayList<>();
        for(int i = 0; i < descriptions.size(); i++) {
            Todo todo = getService().create(
                    i,
                    fakeTodo.getTitle(),
                    descriptions.get(i),
                    fakeTodo.getPriority(),
                    fakeTodo.getPlannedDate()
            );
            result.add(todo);
        }

        for(int i = 0; i < descriptions.size(); i++) {
            assertEquals(result.get(i).getDescription(), descriptions.get(i));
        }
    }

    @Test
    void createWithoutPriority() {
        Todo fakeTodo = getTestEntityHolder().getFakeEntities().get(0);

        Todo todo = getService().create(
                fakeTodo.getTitle(),
                fakeTodo.getDescription(),
                null,
                fakeTodo.getPlannedDate()
        );

        assertNull(todo.getPriority());
    }

    @Test
    void createWithNotPersistedPriority() {
        Priority priorityWithoutId = Priority.builder()
                .name("High")
                .build();
        Priority priorityWithId = Priority.builder()
                .id(0L)
                .name("Low")
                .build();
        List<Priority> priorities = List.of(priorityWithoutId, priorityWithId);
        Todo fakeTodo = getTestEntityHolder().getFakeEntities().get(0);
        String title = fakeTodo.getTitle();
        String description = fakeTodo.getDescription();
        LocalDate plannedDate = fakeTodo.getPlannedDate();

        TodoService service = getService();
        priorities.forEach(priority -> assertAll(
                () -> assertThrows(ApplicationException.class,
                        () -> service.create(
                                title,
                                description,
                                priority,
                                plannedDate
                        )),
                () -> assertEquals(getTestEntityHolder().getEntities().size(), getRepository().count())
        ));
    }

    @Test
    void createWithPastPlannedDate() {
        Todo fakeTodo = getTestEntityHolder().getFakeEntities().get(0);
        String title = fakeTodo.getTitle();
        String description = fakeTodo.getDescription();
        Priority priority = fakeTodo.getPriority();
        LocalDate pastPlannedDate = LocalDate.now().minusDays(1);

        TodoService service = getService();
        assertAll(
                () -> assertThrows(ApplicationException.class,
                        () -> service.create(
                                title,
                                description,
                                priority,
                                pastPlannedDate
                        )),
                () -> assertEquals(getTestEntityHolder().getEntities().size(), getRepository().count())
        );
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    void save() {
        List<Todo> entities = getTestEntityHolder().getEntities();
        Todo todo = entities.get(0);
        String title = todo.getTitle() + "edited";
        String description = todo.getDescription() + "edited";
        Priority priority = priorityService.create("High");
        Status status = StatusWorkflow.get(todo).getNextStatus();
        LocalDate creationDate = todo.getCreationDate();
        LocalDate plannedDate = todo.getPlannedDate().plusDays(1);
        LocalDate completionDate = todo.getCompletionDate();
        todo.setTitle(title);
        todo.setDescription(description);
        todo.setPriority(priority);
        todo.setStatus(status);
        todo.setCreationDate(LocalDate.now().plusDays(1));
        todo.setPlannedDate(plannedDate);
        todo.setCompletionDate(LocalDate.now().plusDays(1));
        todo.setPosition(100L);

        Todo result = getService().save(todo);

        assertAll(
                () -> assertNotNull(result),
                () -> assertNotNull(result.getId()),
                () -> assertEquals(todo.getId(), result.getId()),
                () -> assertEquals(todo.getTitle(), result.getTitle()),
                () -> assertEquals(todo.getDescription(), result.getDescription()),
                () -> assertEquals(todo.getPriority(), result.getPriority()),
                () -> assertEquals(todo.getStatus(), result.getStatus()),
                () -> assertEquals(creationDate, result.getCreationDate()),
                () -> assertEquals(todo.getPlannedDate(), result.getPlannedDate()),
                () -> assertEquals(completionDate, result.getCompletionDate()),
                () -> checkPositions(getRepository(), entities)
        );
        priorityService.deleteAll();
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    void saveWithEmptyTitle() {
        Todo todo = getTestEntityHolder().getEntities().get(0);
        String expected = todo.getTitle();
        List<String> titles = new ArrayList<>();
        titles.add("");
        titles.add("   ");

        assertThrows(NullPointerException.class, () -> todo.setTitle(null));

        TodoService service = getService();
        titles.forEach(title -> {
            todo.setTitle(title);
            assertThrows(ApplicationException.class, () -> service.save(todo));
        });
        assertEquals(expected, service.getById(todo.getId()).getTitle());
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    void saveWithEmptyDescription() {
        Todo todo = getTestEntityHolder().getEntities().get(0);
        List<String> descriptions = new ArrayList<>();
        descriptions.add(null);
        descriptions.add("");
        descriptions.add("   ");

        TodoService service = getService();
        descriptions.forEach(description -> {
            todo.setDescription(description);
            Todo result = service.save(todo);
            assertEquals(description, result.getDescription());
        });
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    void saveWithEmptyPriority() {
        Priority priority = priorityService.create("Test");
        Todo todo = getService().create("Test", "Description", priority, LocalDate.now());
        todo.setPriority(null);

        Todo result = getService().save(todo);
        assertNull(result.getPriority());
        priorityService.deleteAll();
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    void saveWithNotPersistedPriority() {
        Todo todo = getTestEntityHolder().getEntities().get(0);
        Priority expected = todo.getPriority();
        Priority priorityWithoutId = Priority.builder()
                .name("High")
                .build();
        Priority priorityWithId = Priority.builder()
                .id(0L)
                .name("Low")
                .build();
        List<Priority> priorities = List.of(priorityWithoutId, priorityWithId);

        TodoService service = getService();
        priorities.forEach(priority -> {
            todo.setPriority(priority);
            assertAll(
                    () -> assertThrows(ApplicationException.class, () -> service.save(todo)),
                    () -> assertEquals(expected, service.getById(todo.getId()).getPriority())
            );
        });
    }

    @Test
    void saveWithNextStatus() {
        Todo todo = getTestEntityHolder().getEntities().get(0);
        Status nextStatus = StatusWorkflow.get(todo).getNextStatus();
        todo.setStatus(nextStatus);

        Todo result = getService().save(todo);
        assertEquals(nextStatus, result.getStatus());
    }

    @Test
    void saveWithEmptyStatus() {
        Todo todo = getTestEntityHolder().getEntities().get(0);

        assertThrows(NullPointerException.class, () -> todo.setStatus(null));
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    void saveWithPreviousStatus() {
        TodoService service = getService();
        Todo todo = service.create("Test", null, null, LocalDate.now());
        Status previousStatus = todo.getStatus();
        Todo todoWithPreviousStatus = service.save(StatusWorkflow.get(todo).goNextStatus());
        todoWithPreviousStatus.setStatus(previousStatus);

        assertThrows(ApplicationException.class, () -> service.save(todoWithPreviousStatus));
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    void saveWithFinalStatus() {
        TodoService service = getService();
        Todo todo = service.create("Test", null, null, LocalDate.now());

        StatusWorkflow statusWorkflow;
        do {
            todo = service.save(StatusWorkflow.get(todo).goNextStatus());
            statusWorkflow = StatusWorkflow.get(todo);
        }
        while(!statusWorkflow.isFinalStatus());
        Todo finalTodo = todo;

        assertThrows(ApplicationException.class, () -> service.save(finalTodo));
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    void saveWithNotNextStatus() {
        Todo todo = getTestEntityHolder().getEntities().get(0);
        Status expected = todo.getStatus();
        Status nextStatus = StatusWorkflow.get(todo).getNextStatus();
        Status notNextStatus = Arrays.stream(Status.values())
                .filter(status -> status != todo.getStatus() && status != nextStatus)
                .findFirst()
                .orElseThrow();
        todo.setStatus(notNextStatus);

        TodoService service = getService();
        assertAll(
                () -> assertThrows(ApplicationException.class, () -> service.save(todo)),
                () -> assertEquals(expected, service.getById(todo.getId()).getStatus())
        );
    }

    @Test
    void saveWithEmptyCreationDate() {
        Todo todo = getTestEntityHolder().getEntities().get(0);

        assertThrows(NullPointerException.class, () -> todo.setCreationDate(null));
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    void saveWithDifferentCreationDate() {
        Todo todo = getTestEntityHolder().getEntities().get(0);
        LocalDate expected = todo.getCreationDate();
        todo.setCreationDate(LocalDate.now().plusDays(1));

        Todo result = getService().save(todo);
        assertEquals(expected, result.getCreationDate());
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    void saveWithFuturePlannedDate() {
        Todo todo = getTestEntityHolder().getEntities().get(0);
        LocalDate expected = LocalDate.now().plusDays(1);
        todo.setPlannedDate(expected);

        Todo result = getService().save(todo);
        assertEquals(expected, result.getPlannedDate());
    }

    @Test
    void saveWithEmptyPlannedDate() {
        Todo todo = getTestEntityHolder().getEntities().get(0);

        assertThrows(NullPointerException.class, () -> todo.setPlannedDate(null));
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    void saveWithPastPlannedDate() {
        Todo todo = getTestEntityHolder().getEntities().get(0);
        LocalDate expected = todo.getPlannedDate();
        todo.setPlannedDate(LocalDate.now().minusDays(1));

        TodoService service = getService();
        assertAll(
                () -> assertThrows(ApplicationException.class, () -> service.save(todo)),
                () -> assertEquals(expected, service.getById(todo.getId()).getPlannedDate())
        );
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    void saveWithEmptyCompletionDate() {
        Todo todo = getTestEntityHolder().getEntities().get(0);
        LocalDate expected = todo.getCompletionDate();
        todo.setCompletionDate(LocalDate.now());

        TodoService service = getService();
        Todo result = service.save(todo);
        assertEquals(expected, result.getCompletionDate());
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    void saveWithDifferentCompletionDate() {
        Todo todo = getTestEntityHolder().getEntities().get(0);
        LocalDate expected = todo.getCompletionDate();
        todo.setCompletionDate(LocalDate.now().plusDays(1));

        Todo result = getService().save(todo);
        assertEquals(expected, result.getCompletionDate());
    }

    @Test
    void findAllByPriorities() {
        Priority priority = priorityService.create("High");
        List<Todo> entities = getTestEntityHolder().getEntities();
        Todo first = entities.get(0);
        Todo last = entities.get(entities.size() - 1);
        first.setPriority(priority);
        last.setPriority(priority);
        TodoService service = getService();
        entities.forEach(service::save);

        List<Todo> result = service.findAllByPriorities(Collections.singletonList(priority));

        assertAll(
                () -> assertEquals(2, result.size()),
                () -> assertTrue(result.contains(first)),
                () -> assertTrue(result.contains(last))
        );
    }

    @Test
    void findAllByNotPersistedPriorities() {
        Priority priorityWithoutId = Priority.builder().name("High").build();
        Priority priorityWithId = Priority.builder().id(1L).name("High").build();
        List<Priority> priorities = List.of(priorityWithoutId, priorityWithId);

        TodoService service = getService();
        assertThrows(ApplicationException.class, () -> service.findAllByPriorities(priorities));
    }

    @Test
    void findAllByEmptyPriorities() {
        List<Priority> priorities = Collections.emptyList();

        TodoService service = getService();
        assertThrows(ApplicationException.class, () -> service.findAllByPriorities(priorities));
    }
}