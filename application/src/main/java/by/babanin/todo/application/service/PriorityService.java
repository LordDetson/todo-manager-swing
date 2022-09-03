package by.babanin.todo.application.service;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import by.babanin.todo.application.exception.ApplicationException;
import by.babanin.todo.application.repository.PriorityRepository;
import by.babanin.todo.model.Priority;

@Service
public class PriorityService extends AbstractIndexableCrudService<Priority, Long> {

    private static final String FORBIDDEN_SYMBOLS_FOR_NAME = "~`!@#$%^&*_+='\";:()<>[]{}?,./|\\0123456789";
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
        todoService.findAllByPriorities(entitiesToDelete).forEach(todo -> {
            todo.setPriority(null);
            todoService.save(todo);
        });
    }

    @Transactional
    public Priority create(String name) {
        return create(count(), name);
    }

    @Transactional
    public Priority create(long position, String name) {
        validateName(name);
        Priority priority = Priority.builder()
                .name(name)
                .build();
        return insert(position, priority);
    }

    @Transactional
    public Priority rename(Long id, String name) {
        validateName(name);
        Priority priority = getById(id);
        priority.setName(name);
        return getRepository().save(priority);
    }

    private void validateName(String name) {
        if(StringUtils.isBlank(name)) {
            throw new ApplicationException("Name can't be blank");
        }
        List<String> forbiddenSymbolsForName = getForbiddenSymbolsForName();
        for(String forbiddenSymbol : forbiddenSymbolsForName) {
            if(name.contains(forbiddenSymbol)) {
                throw new ApplicationException("Name must not contain characters " + forbiddenSymbolsForName);
            }
        }
        if(name.contains("\n")) {
            throw new ApplicationException("Name must not have a newline character");
        }
        Optional<Priority> found = findByName(name);
        if(found.isPresent()) {
            throw new ApplicationException(name + " priority name already exist");
        }
    }

    @Transactional(readOnly = true)
    public Optional<Priority> findByName(String name) {
        return getRepository().findByName(name);
    }

    public List<String> getForbiddenSymbolsForName() {
        return Arrays.asList(FORBIDDEN_SYMBOLS_FOR_NAME.split(""));
    }
}
