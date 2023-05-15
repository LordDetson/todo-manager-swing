package by.babanin.todo.task.todo;

import java.util.Map;

import org.modelmapper.ModelMapper;

import by.babanin.ext.representation.Representation;
import by.babanin.ext.representation.ReportField;
import by.babanin.todo.application.service.TodoService;
import by.babanin.todo.model.Todo;
import by.babanin.todo.model.Todo.Fields;
import by.babanin.todo.task.SaveTask;
import by.babanin.todo.ui.dto.ToDoInfo;

public class UpdateTodoTask extends SaveTask<Todo, Long, TodoService, ToDoInfo> {

    private final ToDoInfo todo;

    public UpdateTodoTask(
            TodoService service,
            ModelMapper modelMapper,
            Representation<ToDoInfo> representation,
            Map<ReportField, ?> fieldValueMap,
            ToDoInfo todo) {
        super(service, modelMapper, representation, fieldValueMap);
        this.todo = todo;
    }

    @Override
    public Todo body() {
        todo.setTitle(getValue(Fields.title));
        todo.setDescription(getValue(Fields.description));
        todo.setPriority(getValue(Fields.priority));
        todo.setStatus(getValue(Fields.status));
        return getService().save(getModelMapper().map(todo, Todo.class));
    }
}
