package by.babanin.todo.task.todo;

import java.util.Map;

import by.babanin.todo.application.service.TodoService;
import by.babanin.todo.model.Todo;
import by.babanin.todo.model.Todo.Fields;
import by.babanin.todo.representation.ComponentRepresentation;
import by.babanin.todo.representation.ReportField;
import by.babanin.todo.task.SaveTask;

public class CreateTodoTask extends SaveTask<Todo, Long, TodoService, Todo> {

    public CreateTodoTask(TodoService service, ComponentRepresentation<Todo> representation, Map<ReportField, ?> fieldValueMap) {
        super(service, representation, fieldValueMap);
    }

    @Override
    public Todo execute() {
        return getService().create(
                getValue(Fields.title),
                getValue(Fields.description),
                getValue(Fields.priority),
                getValue(Fields.plannedDate)
        );
    }
}
