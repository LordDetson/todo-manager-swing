package by.babanin.todo.view.component.tablemodel;

import by.babanin.todo.application.service.CrudService;
import by.babanin.todo.model.Persistent;

public class TableModelLoader<E extends Persistent<I>, I> {

    private final CrudService<E, I> crudService;

    public TableModelLoader(CrudService<E, I> crudService) {
        this.crudService = crudService;
    }

    public void load(TableModel<E> model) {
        model.addAll(crudService.getAll());
    }
}
