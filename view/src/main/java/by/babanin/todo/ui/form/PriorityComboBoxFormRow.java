package by.babanin.todo.ui.form;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;

import by.babanin.ext.component.form.ComboBoxFormRow;
import by.babanin.ext.representation.ReportField;
import by.babanin.todo.application.service.PriorityService;
import by.babanin.todo.model.Priority;
import by.babanin.todo.task.GetTask;
import by.babanin.todo.ui.dto.PriorityInfo;
import by.babanin.todo.ui.renderer.PriorityComboBoxRenderer;

public class PriorityComboBoxFormRow extends ComboBoxFormRow<PriorityInfo> {

    private final PriorityService priorityService;
    private final ModelMapper modelMapper;

    public PriorityComboBoxFormRow(ReportField field, PriorityService priorityService, ModelMapper modelMapper) {
        super(field);
        this.priorityService = priorityService;
        this.modelMapper = modelMapper;
        setAllowNone(true);
        setRenderer(new PriorityComboBoxRenderer());
        loadPriorities();
    }

    private void loadPriorities() {
        GetTask<Priority, Long, PriorityService, PriorityInfo> task = new GetTask<>(priorityService, modelMapper, new TypeToken<List<PriorityInfo>>() {}.getType());
        task.addFinishListener(items -> {
            setItems(items);
            ignoreStatusChange();
            setNewValue(getCurrentValue());
            enableStatusChange();
        });
        task.execute();
    }
}
