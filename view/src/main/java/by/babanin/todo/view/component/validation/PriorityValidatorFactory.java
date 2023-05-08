package by.babanin.todo.view.component.validation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import by.babanin.ext.message.Translator;
import by.babanin.todo.application.service.PriorityService;
import by.babanin.todo.model.Priority;
import by.babanin.todo.model.Priority.Fields;
import by.babanin.todo.representation.ComponentRepresentation;
import by.babanin.todo.representation.ReportField;
import by.babanin.todo.view.translat.AppTranslateCode;
import by.babanin.todo.view.translat.AppTranslator;

public class PriorityValidatorFactory implements ValidatorFactory {

    private static final int NAME_LENGTH_LIMIT = 16;

    private final PriorityService priorityService;

    public PriorityValidatorFactory(PriorityService priorityService) {
        this.priorityService = priorityService;
    }

    @Override
    public List<Validator> factor(ReportField field) {
        List<Validator> validators = new ArrayList<>();
        ComponentRepresentation<Priority> representation = ComponentRepresentation.get(Priority.class);
        if(representation.getFields().contains(field) && field.getName().equals(Fields.name)) {
            String fieldCaption = AppTranslator.getFieldCaption(field);
            if(field.isMandatory()) {
                validators.add(new MandatoryValueValidator(fieldCaption));
            }
            validators.add(new UniqueNameValidator(representation.getComponentClass(),
                    name -> priorityService.findByName(name).isPresent()));
            validators.add(new LengthLimitValidation(fieldCaption, NAME_LENGTH_LIMIT));
            validators.add(new ForbiddenSymbolsValidator(fieldCaption, priorityService.getForbiddenSymbolsForName()));
            String unprioritized = Translator.toLocale(AppTranslateCode.PRIORITY_UNPRIORITIZED);
            validators.add(new ReservedWordsValidator(Collections.singletonList(unprioritized)));
            validators.add(new AsciiAndRussianValidator(fieldCaption));
        }
        return validators;
    }
}
