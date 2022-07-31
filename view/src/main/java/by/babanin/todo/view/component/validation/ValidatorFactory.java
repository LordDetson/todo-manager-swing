package by.babanin.todo.view.component.validation;

import java.util.List;

import by.babanin.todo.representation.ReportField;

@FunctionalInterface
public interface ValidatorFactory {

    List<Validator> factor(ReportField field);
}
