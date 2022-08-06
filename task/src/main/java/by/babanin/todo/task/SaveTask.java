package by.babanin.todo.task;

import java.util.Map;

import by.babanin.todo.application.service.CrudService;
import by.babanin.todo.model.Persistent;
import by.babanin.todo.representation.ComponentRepresentation;
import by.babanin.todo.representation.ReportField;

public abstract class SaveTask<E extends Persistent<I>, I, S extends CrudService<E, I>, R> extends ServiceTask<E, I, S, R> {

    private final ComponentRepresentation<E> representation;
    private final Map<ReportField, ?> fieldValueMap;

    protected SaveTask(S service, ComponentRepresentation<E> representation, Map<ReportField, ?> fieldValueMap) {
        super(service);
        this.representation = representation;
        this.fieldValueMap = fieldValueMap;
    }

    @SuppressWarnings("unchecked")
    protected <V> V getValue(String fieldName) {
        return (V) fieldValueMap.get(representation.getField(fieldName));
    }
}
