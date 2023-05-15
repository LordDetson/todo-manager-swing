package by.babanin.todo.task;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.modelmapper.ModelMapper;

import by.babanin.todo.application.service.CrudService;
import by.babanin.todo.model.Persistent;

public class GetTask<E extends Persistent<I>, I, S extends CrudService<E, I>, R>
        extends ServiceTask<E, I, S, List<R>> {

    private final Set<I> ids;

    public GetTask(S service, ModelMapper modelMapper, Type resultType) {
        this(service, modelMapper, resultType, null);
    }

    public GetTask(S service, ModelMapper modelMapper, Type resultType, Set<I> ids) {
        super(service, modelMapper, resultType);
        this.ids = ids;
    }

    @Override
    public List<E> body() {
        S service = getService();
        return CollectionUtils.isEmpty(ids) ?
                service.getAll() :
                service.getAllById(ids);
    }
}
