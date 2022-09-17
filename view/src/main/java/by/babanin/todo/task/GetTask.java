package by.babanin.todo.task;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import by.babanin.todo.application.service.CrudService;
import by.babanin.todo.model.Persistent;

public class GetTask<E extends Persistent<I>, I, S extends CrudService<E, I>> extends ServiceTask<E, I, S, List<E>> {
    private final Set<I> ids = new LinkedHashSet<>();

    public GetTask(S service) {
        super(service);
    }

    public GetTask(S service, Set<I> ids) {
        super(service);
        this.ids.addAll(ids);
    }

    @Override
    public List<E> body() {
        S service = getService();
        if(ids.isEmpty()) {
            return service.getAll();
        }
        else {
            return service.getAllById(ids);
        }
    }
}
