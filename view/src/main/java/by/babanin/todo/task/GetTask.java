package by.babanin.todo.task;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import by.babanin.todo.application.service.CrudService;
import by.babanin.todo.model.Persistent;

public class GetTask<E extends Persistent<I>, I> extends AbstractTask<List<E>> {

    private final CrudService<E, I> crudService;
    private final Set<I> ids = new LinkedHashSet<>();

    public GetTask(CrudService<E, I> crudService) {
        this.crudService = crudService;
    }

    public GetTask(CrudService<E, I> crudService, Collection<I> ids) {
        this.crudService = crudService;
        this.ids.addAll(ids);
    }

    @Override
    public List<E> execute() {
        if(ids.isEmpty()) {
            return crudService.getAll();
        }
        else {
            return crudService.getAllById(ids);
        }
    }
}
