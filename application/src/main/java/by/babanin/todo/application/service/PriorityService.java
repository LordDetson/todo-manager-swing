package by.babanin.todo.application.service;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import by.babanin.todo.application.exception.ApplicationException;
import by.babanin.todo.application.repository.PriorityRepository;
import by.babanin.todo.model.Priority;

@Component
public class PriorityService extends AbstractCrudService<Priority, Long> {

    private final PriorityRepository priorityRepository;

    public PriorityService(PriorityRepository priorityRepository) {
        this.priorityRepository = priorityRepository;
    }

    @Override
    public Priority save(Priority entity) {
        return priorityRepository.save(entity);
    }

    @Override
    public void deleteAll() {
        priorityRepository.deleteAll();
    }

    @Override
    public void deleteAllById(Set<Long> ids) {
        boolean existAllIds = ids.stream()
                .allMatch(this::existById);
        if(!existAllIds) {
            throw new ApplicationException("Doesn't have such ids");
        }
        priorityRepository.deleteAllById(ids);
    }

    @Override
    public List<Priority> getAll() {
        return Collections.unmodifiableList(priorityRepository.findAll());
    }

    @Override
    public List<Priority> getAllById(Set<Long> ids) {
        return priorityRepository.findAllById(ids);
    }

    @Override
    public Priority getById(Long id) {
        if(!existById(id)) {
            throw new ApplicationException("No such priority");
        }
        return priorityRepository.getReferenceById(id);
    }

    @Override
    public boolean existById(Long id) {
        return priorityRepository.existsById(id);
    }
}
