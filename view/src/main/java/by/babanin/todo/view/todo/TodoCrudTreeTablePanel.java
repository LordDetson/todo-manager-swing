package by.babanin.todo.view.todo;

import java.time.LocalDate;
import java.util.Collections;

import by.babanin.todo.application.service.TodoService;
import by.babanin.todo.application.status.StatusWorkflow;
import by.babanin.todo.model.Priority;
import by.babanin.todo.model.Status;
import by.babanin.todo.model.Todo;
import by.babanin.todo.model.Todo.Fields;
import by.babanin.todo.representation.ComponentRepresentation;
import by.babanin.todo.representation.ReportField;
import by.babanin.todo.view.component.CrudTablePanel;
import by.babanin.todo.view.component.crud.Crud;
import by.babanin.todo.view.component.tree.TreeTable;
import by.babanin.todo.view.renderer.LocalDataRenderer;
import by.babanin.todo.view.renderer.PriorityRenderer;
import by.babanin.todo.view.renderer.StatusRenderer;

public class TodoCrudTreeTablePanel extends CrudTablePanel<Todo, Long, TodoService, TreeTable, TodoTreeTableModel> {

    public TodoCrudTreeTablePanel(Crud<Todo, Long, TodoService> crud) {
        super(crud);
    }

    @Override
    protected TodoTreeTableModel createTableModel(ComponentRepresentation<Todo> representation) {
        return new TodoTreeTableModel(representation);
    }

    @Override
    protected TreeTable createTable(TodoTreeTableModel model) {
        TreeTable table = new TreeTable();
        table.setTreeTableModel(model);
        table.setRootVisible(false);
        table.setExpandRootChildrenAutomatically(true);
        table.setDefaultRenderer(Priority.class, new PriorityRenderer());
        table.setDefaultRenderer(Status.class, new StatusRenderer());
        table.setDefaultRenderer(LocalDate.class, new LocalDataRenderer());
        return table;
    }

    @Override
    public void addListeners() {
        getCrud().addReadListener(todos -> {
            ReportField field = getCrud().getRepresentation().getField(Fields.status);
            getModel().setGroupByFields(Collections.singletonList(field));
        });
        super.addListeners();
        getCrud().setCanUpdateFunction(todo -> !StatusWorkflow.get(todo).isFinalStatus());
    }

    @Override
    protected void handleCreation(Todo result) {
        getModel().add((int) result.getPosition(), result);
        selectComponent(result);
    }
}
