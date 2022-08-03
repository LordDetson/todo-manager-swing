package by.babanin.todo.view.component.form;

import by.babanin.todo.representation.ReportField;

@FunctionalInterface
public interface FormRowFactory {

    FormRow<Object> factor(ReportField field);
}
