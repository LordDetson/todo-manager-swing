package by.babanin.todo.task;

import by.babanin.todo.application.service.IndexableCrudService;
import by.babanin.todo.model.Indexable;
import by.babanin.todo.model.Persistent;

public class SwapTask<C extends Persistent<I> & Indexable, I, S extends IndexableCrudService<C, I>> extends ServiceTask<C, I, S, Void> {

    private final long position1;
    private final long position2;

    public SwapTask(S service, long position1, long position2) {
        super(service);
        this.position1 = position1;
        this.position2 = position2;
    }

    @Override
    public Void execute() {
        getService().swap(position1, position2);
        return null;
    }
}
