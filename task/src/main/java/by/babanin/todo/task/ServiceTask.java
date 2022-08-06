package by.babanin.todo.task;

import by.babanin.todo.application.service.CrudService;
import by.babanin.todo.model.Persistent;

public abstract class ServiceTask<E extends Persistent<I>, I, S extends CrudService<E, I>, R> extends AbstractTask<R> {

    private final S service;

    protected ServiceTask(S service) {
        this.service = service;
    }

    protected S getService() {
        return service;
    }
}
