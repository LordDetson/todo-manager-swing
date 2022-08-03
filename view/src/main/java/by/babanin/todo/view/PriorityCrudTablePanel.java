package by.babanin.todo.view;

import java.util.Map;

import by.babanin.todo.application.service.PriorityService;
import by.babanin.todo.model.Priority;
import by.babanin.todo.model.Priority.Fields;
import by.babanin.todo.model.Priority.PriorityBuilder;
import by.babanin.todo.representation.ComponentRepresentation;
import by.babanin.todo.representation.ReportField;
import by.babanin.todo.view.component.CrudStyle;
import by.babanin.todo.view.component.CrudTablePanel;
import by.babanin.todo.view.component.form.PriorityFormRowFactory;
import by.babanin.todo.view.component.validation.PriorityValidatorFactory;
import by.babanin.todo.view.util.ServiceHolder;

public class PriorityCrudTablePanel extends CrudTablePanel<Priority, Long> {

    public PriorityCrudTablePanel() {
        super(Priority.class, new PriorityFormRowFactory(), new CrudStyle()
                .setValidatorFactory(new PriorityValidatorFactory()));
    }

    @Override
    protected Priority createComponent(Map<ReportField, ?> fieldValueMap, Priority oldComponent) {
        ComponentRepresentation<Priority> representation = ComponentRepresentation.get(Priority.class);
        PriorityBuilder builder = Priority.builder()
                .name((String) fieldValueMap.get(representation.getField(Fields.name)));
        if(oldComponent != null) {
            builder.weight(oldComponent.getWeight());
        }
        else {
            PriorityService priorityService = ServiceHolder.getPriorityService();
            builder.weight(priorityService.count());
        }
        return builder.build();
    }
}
