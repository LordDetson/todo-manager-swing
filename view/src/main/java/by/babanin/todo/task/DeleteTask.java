package by.babanin.todo.task;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.modelmapper.ModelMapper;

import by.babanin.todo.application.service.CrudService;
import by.babanin.todo.model.Persistent;

public class DeleteTask<E extends Persistent<I>, I, S extends CrudService<E, I>, R>
        extends ServiceTask<E, I, S, List<R>> {

    private final Set<I> ids = new LinkedHashSet<>();

    public DeleteTask(S service, ModelMapper modelMapper, Type resultType, Collection<I> ids) {
        super(service, modelMapper, resultType);
        this.ids.addAll(ids);
    }

    @Override
    public List<E> body() {
        return getService().deleteAllById(ids);
    }
}
