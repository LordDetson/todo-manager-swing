package by.babanin.todo.application.service;

import java.time.LocalDate;
import java.util.ArrayList;
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
        if(StatusWorkflow.get(todoFromDB).isFinalStatus()) {
            throw new ApplicationException("\"" + todo.getTitle() + "\" todo is no longer editable because it's final");
        }
        String title = todo.getTitle();
        if(!Objects.equals(todoFromDB.getTitle(), title)) {
            validateTitle(title);
            todoFromDB.setTitle(title);
        }
        Priority priority = todo.getPriority();
        if(!Objects.equals(todoFromDB.getPriority(), priority)) {
            validatePriority(priority);
            todoFromDB.setPriority(priority);
        }
        LocalDate plannedDate = todo.getPlannedDate();
        if(!Objects.equals(todoFromDB.getPlannedDate(), plannedDate)) {
            validatePannedDate(plannedDate);
            todoFromDB.setPlannedDate(plannedDate);
        }
        Status status = todo.getStatus();
        if(!Objects.equals(todoFromDB.getStatus(), status)) {
            validateStatus(todoFromDB, status);
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

    private void validateStatus(Todo todo, Status newStatus) {
        StatusWorkflow statusWorkflow = StatusWorkflow.get(todo);
        if(newStatus != statusWorkflow.getNextStatus()) {
            throw new ApplicationException(newStatus + " status can't be applied because it isn't next status");
        }
    }

    @Transactional(readOnly = true)
    public List<Todo> findAllByPriorities(Collection<Priority> priorities) {
        List<Todo> result = new ArrayList<>();
        if(!priorities.isEmpty()) {
            List<Priority> notExistedPriorities = priorities.stream()
                    .filter(priority -> priority.getId() == null || !priorityRepository.existsById(priority.getId()))
                    .toList();
            if(!notExistedPriorities.isEmpty()) {
                StringJoiner joiner = new StringJoiner(", ");
                priorities.forEach(priority -> joiner.add(priority.getName()));
                throw new ApplicationException("\"" + joiner + "\" priorities are not existed");
            }
            result.addAll(getRepository().findAllByPriorities(priorities));
        }
        return result;
    }

    @Transactional
    public void removePrioritiesFromTodos(Collection<Priority> priorities) {
        List<Todo> todos = findAllByPriorities(priorities);
        todos.forEach(todo -> todo.setPriority(null));
        getRepository().saveAll(todos);
    }
}
