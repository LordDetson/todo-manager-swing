package by.babanin.todo.ui.form;

import java.util.List;

import org.modelmapper.ModelMapper;

import by.babanin.ext.component.form.ComboBoxFormRow;
import by.babanin.ext.representation.ReportField;
import by.babanin.todo.application.status.StatusWorkflow;
import by.babanin.todo.model.Status;
import by.babanin.todo.model.Todo;
import by.babanin.todo.ui.dto.ToDoInfo;
import by.babanin.todo.ui.renderer.StatusComboBoxRenderer;

public class StatusComboBoxFormRow extends ComboBoxFormRow<Status> {

    private final ModelMapper modelMapper;

    public StatusComboBoxFormRow(ReportField field, ModelMapper modelMapper) {
        super(field);
        this.modelMapper = modelMapper;
        setRenderer(new StatusComboBoxRenderer());
    }

    @Override
    public void setComponent(Object component) {
        super.setComponent(component);
        if(component instanceof ToDoInfo todo) {
            Status current = todo.getStatus();
            Status nextStatus = StatusWorkflow.get(modelMapper.map(todo, Todo.class)).getNextStatus();
            setItems(List.of(current, nextStatus));
        }
    }
}
