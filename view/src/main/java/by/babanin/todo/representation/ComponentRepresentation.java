package by.babanin.todo.representation;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import by.babanin.todo.model.Priority;
import by.babanin.todo.model.ReportableField;
import by.babanin.todo.model.Todo;
import by.babanin.todo.view.exception.ViewException;

public class ComponentRepresentation<C> {

    private static final Map<Class<?>, ComponentRepresentation<?>> COMPONENT_REPRESENTATION_MAP = new HashMap<>();

    public static void initializeComponentRepresentationMap() {
        register(Priority.class);
        register(Todo.class);
    }

    private static <T> void register(Class<T> componentClass) {
        COMPONENT_REPRESENTATION_MAP.put(componentClass, new ComponentRepresentation<>(componentClass));
    }

    @SuppressWarnings("unchecked")
    public static <T> ComponentRepresentation<T> get(Class<T> componentClass) {
        ComponentRepresentation<T> representation = (ComponentRepresentation<T>) COMPONENT_REPRESENTATION_MAP.get(componentClass);
        if(representation == null) {
            throw new ViewException("Component representation wasn't registered for " + componentClass.getName());
        }
        return representation;
    }

    private final Class<C> componentClass;
    private final List<ReportField> fields;

    public ComponentRepresentation(Class<C> componentClass) {
        this.componentClass = componentClass;
        this.fields = collectReportFields();
    }

    private List<ReportField> collectReportFields() {
        return Stream.of(componentClass.getDeclaredFields())
                .filter(field -> field.getAnnotation(ReportableField.class) != null)
                .map(ReportField::new)
                .sorted(Comparator.comparingLong(ReportField::getIndex))
                .toList();
    }

    public Class<C> getComponentClass() {
        return componentClass;
    }

    public List<ReportField> getFields() {
        return fields;
    }

    public ReportField getField(String name) {
        return fields.stream()
                .filter(field -> field.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new ViewException(name + " doesn't exist"));
    }

    public Object getValueAt(C component, ReportField field) {
        return field.getValue(component);
    }
}
