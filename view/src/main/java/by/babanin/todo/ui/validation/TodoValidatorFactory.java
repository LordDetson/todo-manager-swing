package by.babanin.todo.ui.validation;

import java.util.ArrayList;
import java.util.List;

import by.babanin.ext.component.validation.AsciiAndRussianValidator;
import by.babanin.ext.component.validation.LengthLimitValidation;
import by.babanin.ext.component.validation.MandatoryValueValidator;
import by.babanin.ext.component.validation.Validator;
import by.babanin.ext.component.validation.ValidatorFactory;
import by.babanin.ext.message.Translator;
import by.babanin.ext.representation.RepresentationRegister;
import by.babanin.ext.representation.Representation;
import by.babanin.ext.representation.ReportField;
import by.babanin.todo.ui.dto.ToDoInfo;

public class TodoValidatorFactory implements ValidatorFactory {

    private static final int TITLE_LENGTH_LIMIT = 32;
    private static final int DESCRIPTION_LENGTH_LIMIT = 1024;

    @Override
    public List<Validator> factor(ReportField field) {
        List<Validator> validators = new ArrayList<>();
        Representation<ToDoInfo> representation = RepresentationRegister.get(ToDoInfo.class);
        if(representation.getFields().contains(field)) {
            String fieldName = field.getName();
            String fieldCaption = Translator.getFieldCaption(field);
            if(field.isMandatory()) {
                validators.add(new MandatoryValueValidator(fieldCaption));
            }
            if(fieldName.equals(ToDoInfo.Fields.title)) {
                validators.add(new LengthLimitValidation(fieldCaption, TITLE_LENGTH_LIMIT));
                validators.add(new AsciiAndRussianValidator(fieldCaption));
            }
            else if(fieldName.equals(ToDoInfo.Fields.description)) {
                validators.add(new LengthLimitValidation(fieldCaption, DESCRIPTION_LENGTH_LIMIT));
                validators.add(new AsciiAndRussianValidator(fieldCaption));
            }
        }
        return validators;
    }
}
