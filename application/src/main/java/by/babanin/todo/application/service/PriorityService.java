package by.babanin.todo.application.service;

import java.util.Collection;
import java.util.Optional;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import by.babanin.todo.application.exception.ApplicationException;
import by.babanin.todo.application.repository.PriorityRepository;
import by.babanin.todo.model.Priority;

@Component
public class PriorityService extends IndexableCrudService<Priority, Long> {

    private static final String FORBIDDEN_SYMBOLS_FOR_NAME = "~`!@#$%^&*()_+='\";:<>?,./|\\0123456789";
    private final TodoService todoService;

    public PriorityService(PriorityRepository priorityRepository, TodoService todoService) {
        super(priorityRepository);
        this.todoService = todoService;
    }

    @Override
    protected PriorityRepository getRepository() {
        return (PriorityRepository) super.getRepository();
    }

    @Override
    protected void updateReferencesBeforeDelete(Collection<Priority> entitiesToDelete) {
        todoService.findAllByPriorities(entitiesToDelete).forEach(todo -> todoService.updatePriority(todo, null));
    }

    @Transactional
    public Priority create(String name) {
        validateName(name);
        Priority priority = Priority.builder()
                .name(name)
                .position(count())
                .build();
        return getRepository().save(priority);
    }

    @Transactional
    public Priority rename(Priority priority, String name) {
        validateName(name);
        priority = getById(priority.getId());
        priority.setName(name);
        return getRepository().save(priority);
    }

    private void validateName(String name) {
        if(StringUtils.isBlank(name)) {
            throw new ApplicationException("Name can't be blank");
        }
        if(StringUtils.isNotBlank(name)) {
            for(char forbiddenSymbol : FORBIDDEN_SYMBOLS_FOR_NAME.toCharArray()) {
                for(char symbol : name.toCharArray()) {
                    if(forbiddenSymbol == symbol) {
                        throw new ApplicationException("Name must not contain characters " + FORBIDDEN_SYMBOLS_FOR_NAME);
                    }
                }
            }
        }
        Optional<Priority> found = findByName(name);
        if(found.isPresent()) {
            throw new ApplicationException(name + " priority name already exist");
        }
    }

    @Transactional
    public Optional<Priority> findByName(String name) {
        return getRepository().findByName(name);
    }
}
