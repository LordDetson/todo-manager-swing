package by.babanin.todo.application;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import by.babanin.todo.application.exception.ApplicationException;
import by.babanin.todo.model.Priority;

@Component
public class PriorityService extends AbstractCrudService<Priority, Long> {

    private final List<Priority> priorities = new ArrayList<>();

    public PriorityService() {
        save(Priority.builder()
                .name("High")
                .weight(0)
                .build());
    }

    @Override
    public Priority save(Priority entity) {
        int insertIndex = priorities.size();
        if(exist(entity)) {
            insertIndex = priorities.indexOf(entity);
        }
        priorities.add(insertIndex, entity);
        return entity;
    }

    @Override
    public void deleteAllById(Set<Long> ids) {
        boolean existAllIds = ids.stream()
                .allMatch(this::existById);
        if(!existAllIds) {
            throw new ApplicationException("Doesn't have such ids");
        }
        priorities.removeIf(priority -> ids.contains(priority.getId()));
    }

    @Override
    public List<Priority> getAll() {
        return Collections.unmodifiableList(priorities);
    }

    @Override
    public Priority getById(Long id) {
        if(!existById(id)) {
            throw new ApplicationException("No such priority");
        }
        return priorities.stream()
                .filter(priority -> priority.getId().equals(id))
                .findFirst().get();
    }

    @Override
    public boolean existById(Long id) {
        return priorities.stream()
                .anyMatch(priority -> priority.getId().equals(id));
    }
}
