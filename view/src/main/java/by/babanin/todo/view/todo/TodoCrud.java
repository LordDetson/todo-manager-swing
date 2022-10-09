package by.babanin.todo.view.todo;

import java.awt.Component;
import java.util.Map;

import by.babanin.todo.application.service.TodoService;
import by.babanin.todo.model.Todo;
import by.babanin.todo.representation.ComponentRepresentation;
import by.babanin.todo.representation.ReportField;
import by.babanin.todo.task.Task;
import by.babanin.todo.task.todo.CreateTodoTask;
import by.babanin.todo.task.todo.UpdateTodoTask;
import by.babanin.todo.view.component.CrudStyle;
import by.babanin.todo.view.component.crud.Crud;
import by.babanin.todo.view.component.form.FormRowFactory;

public class TodoCrud extends Crud<Todo, Long, TodoService> {

    public TodoCrud(Component owner, TodoService crudService,
            ComponentRepresentation<Todo> representation,
            FormRowFactory formRowFactory, CrudStyle crudStyle) {
        super(owner, crudService, representation, formRowFactory, crudStyle);
    }

    @Override
    protected Task<Todo> createCreationTask(Map<ReportField, ?> fieldValueMap) {
        return new CreateTodoTask(getCrudService(), getRepresentation(), fieldValueMap);
    }

    @Override
    protected Task<Todo> createUpdateTask(Map<ReportField, ?> fieldValueMap, Todo todo) {
        return new UpdateTodoTask(getCrudService(), getRepresentation(), fieldValueMap, todo);
    }
}
