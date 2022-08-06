package by.babanin.todo.task.todo;

import java.util.Map;

import by.babanin.todo.application.service.TodoService;
import by.babanin.todo.model.Todo;
import by.babanin.todo.model.Todo.Fields;
import by.babanin.todo.representation.ComponentRepresentation;
import by.babanin.todo.representation.ReportField;
import by.babanin.todo.task.SaveTask;

public class UpdateTodoTask extends SaveTask<Todo, Long, TodoService, Todo> {
    private final Todo oldTodo;

    public UpdateTodoTask(TodoService service, ComponentRepresentation<Todo> representation, Map<ReportField, ?> fieldValueMap,
            Todo oldTodo) {
        super(service, representation, fieldValueMap);
        this.oldTodo = oldTodo;
    }

    @Override

    public Todo execute() {
        return getService().update(oldTodo,
                getValue(Fields.title),
                getValue(Fields.description),
                getValue(Fields.priority),
                getValue(Fields.status)
        );
    }
}
