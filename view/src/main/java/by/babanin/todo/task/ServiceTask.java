package by.babanin.todo.task;

import java.lang.reflect.Type;

import org.modelmapper.ModelMapper;

import by.babanin.ext.task.AbstractTask;
import by.babanin.todo.application.service.CrudService;
import by.babanin.todo.model.Persistent;

public abstract class ServiceTask<E extends Persistent<I>, I, S extends CrudService<E, I>, R>
        extends AbstractTask<R> {

    private final S service;
    private final ModelMapper modelMapper;
    private final Type resultType;

    protected ServiceTask(S service, ModelMapper modelMapper, Type resultType) {
        this.service = service;
        this.modelMapper = modelMapper;
        this.resultType = resultType;
    }

    protected S getService() {
        return service;
    }

    protected ModelMapper getModelMapper() {
        return modelMapper;
    }

    @Override
    protected R doInBackground() throws Exception {
        Object result = body();
        if(result != null) {
            return mapResult(result);
        }
        return null;
    }

    protected abstract Object body() throws Exception;

    protected R mapResult(Object result) {
        return modelMapper.map(result, resultType);
    }
}
