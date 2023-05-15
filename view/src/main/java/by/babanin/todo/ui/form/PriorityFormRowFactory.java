package by.babanin.todo.ui.form;

import by.babanin.ext.component.exception.UIException;
import by.babanin.ext.component.form.FormRow;
import by.babanin.ext.component.form.FormRowFactory;
import by.babanin.ext.component.form.TextFormRow;
import by.babanin.ext.representation.ReportField;
import by.babanin.ext.representation.Representation;
import by.babanin.ext.representation.RepresentationRegister;
import by.babanin.todo.ui.dto.PriorityInfo;

public class PriorityFormRowFactory implements FormRowFactory {

    @SuppressWarnings("unchecked")
    @Override
    public FormRow<Object> factor(ReportField field) {
        Representation<PriorityInfo> representation = RepresentationRegister.get(PriorityInfo.class);
        if(representation.getFields().contains(field) && field.getName().equals(PriorityInfo.Fields.name)) {
            FormRow<?> formRow = new TextFormRow(field);
            return (FormRow<Object>) formRow;
        }
        else {
            throw new UIException(field.getName() + " field of " + representation.getComponentClass().getSimpleName() + "class doesn't support");
        }
    }
}
