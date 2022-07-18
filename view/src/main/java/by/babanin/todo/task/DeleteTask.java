package by.babanin.todo.task;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import by.babanin.todo.application.service.CrudService;
import by.babanin.todo.model.Persistent;

public class DeleteTask<E extends Persistent<I>, I> extends AbstractTask<List<E>> {

    private final CrudService<E, I> crudService;
    private final Set<I> ids = new LinkedHashSet<>();

    public DeleteTask(CrudService<E, I> crudService, Collection<I> ids) {
        this.crudService = crudService;
        this.ids.addAll(ids);
    }

    @Override
    public List<E> execute() {
        List<E> entities = crudService.getAllById(ids);
        crudService.deleteAllById(ids);
        return entities;
    }
}
