package by.babanin.todo.task;

import java.util.Map;

import org.modelmapper.ModelMapper;

import by.babanin.ext.representation.Representation;
import by.babanin.ext.representation.ReportField;
import by.babanin.todo.application.service.CrudService;
import by.babanin.todo.model.Persistent;

public abstract class SaveTask<E extends Persistent<I>, I, S extends CrudService<E, I>, R>
        extends ServiceTask<E, I, S, R> {

    private final Representation<R> representation;
    private final Map<ReportField, ?> fieldValueMap;

    protected SaveTask(
            S service,
            ModelMapper modelMapper,
            Representation<R> representation,
            Map<ReportField, ?> fieldValueMap
    ) {
        super(service, modelMapper, representation.getComponentClass());
        this.representation = representation;
        this.fieldValueMap = fieldValueMap;
    }

    @SuppressWarnings("unchecked")
    protected <V> V getValue(String fieldName) {
        return (V) fieldValueMap.get(representation.getField(fieldName));
    }
}
