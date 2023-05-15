package by.babanin.todo.ui.validation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import by.babanin.ext.component.validation.AsciiAndRussianValidator;
import by.babanin.ext.component.validation.ForbiddenSymbolsValidator;
import by.babanin.ext.component.validation.LengthLimitValidation;
import by.babanin.ext.component.validation.MandatoryValueValidator;
import by.babanin.ext.component.validation.ReservedWordsValidator;
import by.babanin.ext.component.validation.UniqueNameValidator;
import by.babanin.ext.component.validation.Validator;
import by.babanin.ext.component.validation.ValidatorFactory;
import by.babanin.ext.message.Translator;
import by.babanin.ext.representation.RepresentationRegister;
import by.babanin.ext.representation.Representation;
import by.babanin.ext.representation.ReportField;
import by.babanin.todo.application.service.PriorityService;
import by.babanin.todo.model.Priority.Fields;
import by.babanin.todo.ui.dto.PriorityInfo;
import by.babanin.todo.ui.translat.AppTranslateCode;

public class PriorityValidatorFactory implements ValidatorFactory {

    private static final int NAME_LENGTH_LIMIT = 16;

    private final PriorityService priorityService;

    public PriorityValidatorFactory(PriorityService priorityService) {
        this.priorityService = priorityService;
    }

    @Override
    public List<Validator> factor(ReportField field) {
        List<Validator> validators = new ArrayList<>();
        Representation<PriorityInfo> representation = RepresentationRegister.get(PriorityInfo.class);
        if(representation.getFields().contains(field) && field.getName().equals(Fields.name)) {
            String fieldCaption = Translator.getFieldCaption(field);
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
