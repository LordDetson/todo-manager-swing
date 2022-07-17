package by.babanin.todo.application.service;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.stereotype.Component;

import by.babanin.todo.application.exception.ApplicationException;
import by.babanin.todo.application.repository.PriorityRepository;
import by.babanin.todo.model.Priority;

@Component
public class PriorityService extends AbstractCrudService<Priority, Long> {

    private final PriorityRepository priorityRepository;
    private final TodoService todoService;

    public PriorityService(PriorityRepository priorityRepository, TodoService todoService) {
        this.priorityRepository = priorityRepository;
        this.todoService = todoService;
    }

    @Transactional
    @Override
    public Priority save(Priority entity) {
        return priorityRepository.save(entity);
    }

    @Transactional
    @Override
    public void deleteAll() {
        priorityRepository.deleteAll();
    }

    @Transactional
    @Override
    public void deleteAllById(Set<Long> ids) {
        List<Priority> prioritiesToDelete = getAllById(ids);
        if(prioritiesToDelete.isEmpty()) {
            throw new ApplicationException("Doesn't have such ids");
        }
        todoService.findAllByPriorities(prioritiesToDelete).forEach(todo -> {
            todo.setPriority(null);
            todoService.save(todo);
        });
        priorityRepository.deleteAllById(ids);
    }

    @Transactional
    @Override
    public List<Priority> getAll() {
        return Collections.unmodifiableList(priorityRepository.findAll());
    }

    @Transactional
    @Override
    public List<Priority> getAllById(Set<Long> ids) {
        return priorityRepository.findAllById(ids);
    }

    @Transactional
    @Override
    public Priority getById(Long id) {
        if(!existById(id)) {
            throw new ApplicationException("No such priority");
        }
        return priorityRepository.getReferenceById(id);
    }

    @Transactional
    @Override
    public boolean existById(Long id) {
        return priorityRepository.existsById(id);
    }

    @Transactional
    @Override
    public long count() {
        return priorityRepository.count();
    }
}
