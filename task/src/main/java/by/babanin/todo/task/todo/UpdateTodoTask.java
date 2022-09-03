package by.babanin.todo.task.todo;

import java.util.Map;

import by.babanin.todo.application.service.TodoService;
import by.babanin.todo.model.Todo;
import by.babanin.todo.model.Todo.Fields;
import by.babanin.todo.representation.ComponentRepresentation;
import by.babanin.todo.representation.ReportField;
import by.babanin.todo.task.SaveTask;

public class UpdateTodoTask extends SaveTask<Todo, Long, TodoService, Todo> {
    private final Todo todo;

    public UpdateTodoTask(TodoService service, ComponentRepresentation<Todo> representation, Map<ReportField, ?> fieldValueMap,
            Todo todo) {
        super(service, representation, fieldValueMap);
        this.todo = todo;
    }

    @Override

    public Todo execute() {
        todo.setTitle(getValue(Fields.title));
        todo.setDescription(getValue(Fields.description));
        todo.setPriority(getValue(Fields.priority));
        todo.setStatus(getValue(Fields.status));
        return getService().save(todo);
    }
}
