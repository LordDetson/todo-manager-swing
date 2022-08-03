package by.babanin.todo.application.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;

import by.babanin.todo.application.exception.ApplicationException;
import by.babanin.todo.application.repository.PriorityRepository;
import by.babanin.todo.model.Priority;
import by.babanin.todo.model.Priority.Fields;

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
        OptionalLong minWeight = prioritiesToDelete.stream()
                .mapToLong(Priority::getWeight)
                .min();
        priorityRepository.deleteAllById(ids);
        if(minWeight.isPresent()) {
            List<Priority> greaterWeightPriorities = priorityRepository.findByWeightGreaterThanOrderByWeightAsc(minWeight.getAsLong());
            for(int i = 0; i < greaterWeightPriorities.size(); i++) {
                greaterWeightPriorities.get(i).setWeight(i + minWeight.getAsLong());
            }
            priorityRepository.saveAll(greaterWeightPriorities);
        }
    }

    @Transactional
    @Override
    public List<Priority> getAll() {
        return Collections.unmodifiableList(priorityRepository.findAll(Sort.by(Direction.ASC, Fields.weight)));
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

    public Optional<Priority> findByName(String name) {
        return priorityRepository.findByName(name);
    }
}
