package by.babanin.todo.task.todo;

import java.util.Map;

import org.modelmapper.ModelMapper;

import by.babanin.ext.representation.Representation;
import by.babanin.ext.representation.ReportField;
import by.babanin.todo.application.service.TodoService;
import by.babanin.todo.model.Priority;
import by.babanin.todo.model.Todo;
import by.babanin.todo.model.Todo.Fields;
import by.babanin.todo.task.SaveTask;
import by.babanin.todo.ui.dto.PriorityInfo;
import by.babanin.todo.ui.dto.ToDoInfo;

public class CreateTodoTask extends SaveTask<Todo, Long, TodoService, ToDoInfo> {

    public CreateTodoTask(TodoService service, ModelMapper modelMapper, Representation<ToDoInfo> representation, Map<ReportField, ?> fieldValueMap) {
        super(service, modelMapper, representation, fieldValueMap);
    }

    @Override
    public Todo body() {
        PriorityInfo priorityInfo = getValue(Fields.priority);
        Priority priority = null;
        if(priorityInfo != null) {
            priority = getModelMapper().map(priorityInfo, Priority.class);
        }
        return getService().create(
                getValue(Fields.title),
                getValue(Fields.description),
                priority,
                getValue(Fields.plannedDate)
        );
    }
}
