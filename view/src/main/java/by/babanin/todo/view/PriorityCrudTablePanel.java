package by.babanin.todo.view;

import by.babanin.todo.model.Priority;
import by.babanin.todo.view.component.crud.CrudTablePanel;
import by.babanin.todo.view.component.tablemodel.TableModelLoader;
import by.babanin.todo.view.util.ServiceHolder;

public class PriorityCrudTablePanel extends CrudTablePanel<Priority, Long> {

    public PriorityCrudTablePanel() {
        super(Priority.class, new TableModelLoader<>(ServiceHolder.getPriorityService()));
    }
}
