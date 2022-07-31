package by.babanin.todo.view.component.validation;

import java.util.ArrayList;
import java.util.List;

import by.babanin.todo.model.Todo;
import by.babanin.todo.model.Todo.Fields;
import by.babanin.todo.representation.ComponentRepresentation;
import by.babanin.todo.representation.ReportField;

public class TodoValidatorFactory implements ValidatorFactory {

    private static final int TITLE_LENGTH_LIMIT = 16;
    private static final int DESCRIPTION_LENGTH_LIMIT = 1024;

    @Override
    public List<Validator> factor(ReportField field) {
        List<Validator> validators = new ArrayList<>();
        ComponentRepresentation<Todo> representation = ComponentRepresentation.get(Todo.class);
        if(representation.getFields().contains(field)) {
            String fieldName = field.getName();
            String fieldCaption = field.getCaption();
            if(field.isMandatory()) {
                validators.add(new MandatoryValueValidator(fieldCaption));
            }
            if(fieldName.equals(Fields.title)) {
                validators.add(new LengthLimitValidation(fieldCaption, TITLE_LENGTH_LIMIT));
            }
            else if(fieldName.equals(Fields.description)) {
                validators.add(new LengthLimitValidation(fieldCaption, DESCRIPTION_LENGTH_LIMIT));
            }
        }
        return validators;
    }
}
