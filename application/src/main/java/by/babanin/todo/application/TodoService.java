package by.babanin.todo.application;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import by.babanin.todo.application.exception.ApplicationException;
import by.babanin.todo.model.Priority;
import by.babanin.todo.model.Status;
import by.babanin.todo.model.Todo;

@Component
public class TodoService extends AbstractCrudService<Todo, Long> {

    private final List<Todo> todos = new ArrayList<>();

    public TodoService() {
        save(Todo.builder()
                .title("First todo")
                .description("Description")
                .priority(Priority.builder()
                        .name("High")
                        .weight(15)
                        .build())
                .status(Status.OPEN)
                .creationDate(LocalDate.of(2022, 5, 15))
                .plannedDate(LocalDate.of(2022, 5, 15))
                .completionDate(LocalDate.of(2022, 5, 15))
                .build());
    }

    @Override
    public Todo save(Todo entity) {
        int insertIndex = todos.size();
        if(exist(entity)) {
            insertIndex = todos.indexOf(entity);
        }
        todos.add(insertIndex, entity);
        return entity;
    }

    @Override
    public void deleteAllById(Set<Long> ids) {
        boolean existAllIds = ids.stream()
                .allMatch(this::existById);
        if(!existAllIds) {
            throw new ApplicationException("Doesn't have such ids");
        }
        todos.removeIf(priority -> ids.contains(priority.getId()));
    }

    @Override
    public List<Todo> getAll() {
        return Collections.unmodifiableList(todos);
    }

    @Override
    public Todo getById(Long id) {
        if(!existById(id)) {
            throw new ApplicationException("No such priority");
        }
        return todos.stream()
                .filter(priority -> priority.getId().equals(id))
                .findFirst().get();
    }

    @Override
    public boolean existById(Long id) {
        return todos.stream()
                .anyMatch(priority -> priority.getId().equals(id));
    }
}
