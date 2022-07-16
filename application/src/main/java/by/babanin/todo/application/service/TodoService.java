package by.babanin.todo.application.service;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import by.babanin.todo.application.exception.ApplicationException;
import by.babanin.todo.application.repository.TodoRepository;
import by.babanin.todo.model.Todo;

@Component
public class TodoService extends AbstractCrudService<Todo, Long> {

    private final TodoRepository todoRepository;

    public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    @Override
    public Todo save(Todo entity) {
        return todoRepository.save(entity);
    }

    @Override
    public void deleteAll() {
        todoRepository.deleteAll();
    }

    @Override
    public void deleteAllById(Set<Long> ids) {
        boolean existAllIds = ids.stream()
                .allMatch(this::existById);
        if(!existAllIds) {
            throw new ApplicationException("Doesn't have such ids");
        }
        todoRepository.deleteAllById(ids);
    }

    @Override
    public List<Todo> getAll() {
        return Collections.unmodifiableList(todoRepository.findAll());
    }

    @Override
    public List<Todo> getAllById(Set<Long> ids) {
        return todoRepository.findAllById(ids);
    }

    @Override
    public Todo getById(Long id) {
        if(!existById(id)) {
            throw new ApplicationException("No such priority");
        }
        return todoRepository.getReferenceById(id);
    }

    @Override
    public boolean existById(Long id) {
        return todoRepository.existsById(id);
    }

    @Override
    public long count() {
        return todoRepository.count();
    }
}
