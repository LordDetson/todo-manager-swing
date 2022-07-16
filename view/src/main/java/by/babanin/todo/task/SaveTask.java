package by.babanin.todo.task;

import by.babanin.todo.application.service.CrudService;
import by.babanin.todo.model.Persistent;

public class SaveTask<E extends Persistent<I>, I> extends AbstractTask<E> {

    private final CrudService<E, I> crudService;
    private final E objectToSave;

    public SaveTask(CrudService<E, I> crudService, E objectToSave) {
        this.crudService = crudService;
        this.objectToSave = objectToSave;
    }

    @Override
    public E execute() {
        return crudService.save(objectToSave);
    }
}
