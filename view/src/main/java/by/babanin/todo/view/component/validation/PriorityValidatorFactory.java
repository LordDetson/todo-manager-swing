package by.babanin.todo.view.component.validation;

import java.util.ArrayList;
import java.util.List;

import by.babanin.todo.model.Priority;
import by.babanin.todo.model.Priority.Fields;
import by.babanin.todo.representation.ComponentRepresentation;
import by.babanin.todo.representation.ReportField;
import by.babanin.todo.view.util.ServiceHolder;

public class PriorityValidatorFactory implements ValidatorFactory {

    private static final int NAME_LENGTH_LIMIT = 16;

    @Override
    public List<Validator> factor(ReportField field) {
        List<Validator> validators = new ArrayList<>();
        ComponentRepresentation<Priority> representation = ComponentRepresentation.get(Priority.class);
        if(representation.getFields().contains(field) && field.getName().equals(Fields.name)) {
            String fieldCaption = field.getCaption();
            if(field.isMandatory()) {
                validators.add(new MandatoryValueValidator(fieldCaption));
            }
            validators.add(new UniqueNameValidator(representation.getComponentClass(),
                    name -> ServiceHolder.getPriorityService().findByName(name).isPresent()));
            validators.add(new LengthLimitValidation(fieldCaption, NAME_LENGTH_LIMIT));
            validators.add(new ForbiddenSymbolsValidator(fieldCaption, "~`!@#$%^&*()_+='\";:<>?,./|\\0123456789"));
        }
        return validators;
    }
}
