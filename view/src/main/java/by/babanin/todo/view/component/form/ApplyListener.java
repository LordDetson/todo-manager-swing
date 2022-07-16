package by.babanin.todo.view.component.form;

import java.util.Map;
import java.util.function.Consumer;

import by.babanin.todo.representation.ReportField;

@FunctionalInterface
public interface ApplyListener extends Consumer<Map<ReportField, ?>> {

}
