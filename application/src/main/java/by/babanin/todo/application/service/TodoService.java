package by.babanin.todo.application.service;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import by.babanin.todo.application.exception.ApplicationException;
import by.babanin.todo.application.repository.TodoRepository;
import by.babanin.todo.application.status.StatusWorkflow;
import by.babanin.todo.model.Priority;
import by.babanin.todo.model.Status;
import by.babanin.todo.model.Todo;

@Component
public class TodoService extends IndexableCrudService<Todo, Long> {

    public TodoService(TodoRepository todoRepository) {
        super(todoRepository);
    }

    @Override
    protected TodoRepository getRepository() {
        return (TodoRepository) super.getRepository();
    }

    @Transactional
    public Todo create(String title, String description, Priority priority, LocalDate plannedDate) {
        validateTitle(title);
        if(plannedDate.isBefore(LocalDate.now())) {
            throw new ApplicationException("Planned date can't be before today");
        }
        Todo todo = Todo.builder()
                .title(title)
                .description(description)
                .priority(priority)
                .status(Status.OPEN)
                .creationDate(LocalDate.now())
                .plannedDate(plannedDate)
                .position(count())
                .build();
        return getRepository().save(todo);
    }

    @Transactional
    public Todo update(Todo todo, String title, String description, Priority priority, Status status) {
        validateTitle(title);

        todo = getById(todo.getId());
        StatusWorkflow.validateStatus(todo, status);

        todo.setTitle(title);
        todo.setDescription(description);
        todo.setPriority(priority);
        if(todo.getStatus() != status) {
            todo = StatusWorkflow.get(todo).goNextStatus();
        }
        return getRepository().save(todo);
    }

    private void validateTitle(String title) {
        if(StringUtils.isBlank(title)) {
            throw new ApplicationException("Title can't be blank");
        }
    }

    @Transactional
    public void updatePriority(Todo todo, Priority priority) {
        todo = getById(todo.getId());
        todo.setPriority(priority);
        getRepository().save(todo);
    }

    @Transactional
    public List<Todo> findAllByPriorities(Collection<Priority> priorities) {
        return getRepository().findAllByPriorities(priorities);
    }
}
