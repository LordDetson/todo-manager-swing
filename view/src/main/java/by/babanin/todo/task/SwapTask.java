package by.babanin.todo.task;

import by.babanin.todo.application.service.AbstractIndexableCrudService;
import by.babanin.todo.model.Indexable;
import by.babanin.todo.model.Persistent;

public class SwapTask<C extends Persistent<I> & Indexable, I, S extends AbstractIndexableCrudService<C, I>> extends ServiceTask<C, I, S, Void> {

    private final long position1;
    private final long position2;

    public SwapTask(S service, long position1, long position2) {
        super(service, null, null);
        this.position1 = position1;
        this.position2 = position2;
    }

    @Override
    public Void body() {
        getService().swap(position1, position2);
        return null;
    }
}
