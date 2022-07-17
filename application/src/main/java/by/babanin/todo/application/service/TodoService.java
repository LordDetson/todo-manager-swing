package by.babanin.todo.application.service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.stereotype.Component;

import by.babanin.todo.application.exception.ApplicationException;
import by.babanin.todo.application.repository.TodoRepository;
import by.babanin.todo.model.Priority;
import by.babanin.todo.model.Todo;

@Component
public class TodoService extends AbstractCrudService<Todo, Long> {

    private final TodoRepository todoRepository;

    public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    @Transactional
    @Override
    public Todo save(Todo entity) {
        return todoRepository.save(entity);
    }

    @Transactional
    @Override
    public void deleteAll() {
        todoRepository.deleteAll();
    }

    @Transactional
    @Override
    public void deleteAllById(Set<Long> ids) {
        boolean existAllIds = ids.stream()
                .allMatch(this::existById);
        if(!existAllIds) {
            throw new ApplicationException("Doesn't have such ids");
        }
        todoRepository.deleteAllById(ids);
    }

    @Transactional
    @Override
    public List<Todo> getAll() {
        return Collections.unmodifiableList(todoRepository.findAll());
    }

    @Transactional
    @Override
    public List<Todo> getAllById(Set<Long> ids) {
        return todoRepository.findAllById(ids);
    }

    @Transactional
    @Override
    public Todo getById(Long id) {
        if(!existById(id)) {
            throw new ApplicationException("No such priority");
        }
        return todoRepository.getReferenceById(id);
    }

    @Transactional
    @Override
    public boolean existById(Long id) {
        return todoRepository.existsById(id);
    }

    @Transactional
    @Override
    public long count() {
        return todoRepository.count();
    }

    public List<Todo> findAllByPriorities(Collection<Priority> priorities) {
        return todoRepository.findAllByPriorities(priorities);
    }
}
