package by.babanin.todo.application.service;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import by.babanin.todo.application.exception.ApplicationException;
import by.babanin.todo.application.repository.PriorityRepository;
import by.babanin.todo.application.repository.TodoRepository;
import by.babanin.todo.application.status.StatusWorkflow;
import by.babanin.todo.model.Priority;
import by.babanin.todo.model.Status;
import by.babanin.todo.model.Todo;

@Service
public class TodoService extends AbstractIndexableCrudService<Todo, Long> {

    private final PriorityRepository priorityRepository;

    public TodoService(TodoRepository todoRepository, PriorityRepository priorityRepository) {
        super(todoRepository);
        this.priorityRepository = priorityRepository;
    }

    @Override
    protected TodoRepository getRepository() {
        return (TodoRepository) super.getRepository();
    }

    @Transactional
    public Todo create(String title, String description, Priority priority, LocalDate plannedDate) {
        return create(0, title, description, priority, plannedDate);
    }

    @Transactional
    public Todo create(long position, String title, String description, Priority priority, LocalDate plannedDate) {
        validateTitle(title);
        validatePriority(priority);
        validatePannedDate(plannedDate);

        Todo todo = Todo.builder()
                .title(title)
                .description(description)
                .priority(priority)
                .status(Status.OPEN)
                .creationDate(LocalDate.now())
                .plannedDate(plannedDate)
                .build();
        return insert(position, todo);
    }

    @Transactional
    public Todo save(Todo todo) {
        Todo todoFromDB = getById(todo.getId());
        if(!Objects.equals(todoFromDB.getTitle(), todo.getTitle())) {
            validateTitle(todo.getTitle());
            todoFromDB.setTitle(todo.getTitle());
        }
        if(!Objects.equals(todoFromDB.getPriority(), todo.getPriority())) {
            validatePriority(todo.getPriority());
            todoFromDB.setPriority(todo.getPriority());
        }
        if(!Objects.equals(todoFromDB.getPlannedDate(), todo.getPlannedDate())) {
            validatePannedDate(todo.getPlannedDate());
            todoFromDB.setPlannedDate(todo.getPlannedDate());
        }
        if(!Objects.equals(todoFromDB.getStatus(), todo.getStatus())) {
            StatusWorkflow.validateStatus(todoFromDB, todo.getStatus());
            todoFromDB = StatusWorkflow.get(todoFromDB).goNextStatus();
        }
        todoFromDB.setDescription(todo.getDescription());
        return getRepository().save(todoFromDB);
    }

    private void validateTitle(String title) {
        if(StringUtils.isBlank(title)) {
            throw new ApplicationException("Title can't be blank");
        }
    }

    private void validatePriority(Priority priority) {
        if(priority != null) {
            if(priority.getId() == null) {
                throw new ApplicationException("Priority should have id");
            }
            else if(!priorityRepository.existsById(priority.getId())) {
                throw new ApplicationException("Priority should be persisted");
            }
        }
    }

    private void validatePannedDate(LocalDate plannedDate) {
        if(plannedDate.isBefore(LocalDate.now())) {
            throw new ApplicationException("Planned date can't be before today");
        }
    }

    @Transactional(readOnly = true)
    public List<Todo> findAllByPriorities(Collection<Priority> priorities) {
        if(priorities.isEmpty()) {
            throw new ApplicationException("Priorities list is empty");
        }
        List<Priority> notExistedPriorities = priorities.stream()
                .filter(priority -> priority.getId() == null || !priorityRepository.existsById(priority.getId()))
                .toList();
        if(!notExistedPriorities.isEmpty()) {
            StringJoiner joiner = new StringJoiner(", ");
            priorities.forEach(priority -> joiner.add(priority.getName()));
            throw new ApplicationException("\"" + joiner + "\" priorities are not existed");
        }
        return getRepository().findAllByPriorities(priorities);
    }
}
