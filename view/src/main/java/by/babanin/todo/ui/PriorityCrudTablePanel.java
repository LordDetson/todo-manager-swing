package by.babanin.todo.ui;

import java.util.List;
import java.util.Map;

import javax.swing.JPopupMenu;
import javax.swing.JTable;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import by.babanin.ext.component.CrudStyle;
import by.babanin.ext.component.CustomTableColumnModel;
import by.babanin.ext.component.MovableCrudTablePanel;
import by.babanin.ext.component.TableModel;
import by.babanin.ext.representation.ReportField;
import by.babanin.ext.task.Task;
import by.babanin.todo.application.service.PriorityService;
import by.babanin.todo.task.DeleteTask;
import by.babanin.todo.task.GetTask;
import by.babanin.todo.task.SwapTask;
import by.babanin.todo.task.priority.CreatePriorityTask;
import by.babanin.todo.task.priority.UpdatePriorityTask;
import by.babanin.todo.ui.form.PriorityFormRowFactory;
import by.babanin.todo.ui.validation.PriorityValidatorFactory;
import by.babanin.todo.ui.dto.PriorityInfo;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public final class PriorityCrudTablePanel extends MovableCrudTablePanel<PriorityInfo> {

    private final PriorityService priorityService;
    private final ModelMapper modelMapper;

    public PriorityCrudTablePanel(PriorityService priorityService, ModelMapper modelMapper) {
        super(PriorityInfo.class, new PriorityFormRowFactory(), new CrudStyle()
                .setValidatorFactory(new PriorityValidatorFactory(priorityService)));
        this.priorityService = priorityService;
        this.modelMapper = modelMapper;
        setName("priorityCrudTablePanel");
    }

    @Override
    protected void setupTable(JTable table, TableModel<PriorityInfo> model, CustomTableColumnModel columnModel, JPopupMenu popupMenu) {
        super.setupTable(table, model, columnModel, popupMenu);
        setDefaultColumnIdsToFit(PriorityInfo.Fields.name);
    }

    @Override
    protected Task<List<PriorityInfo>> createLoadTask() {
        return new GetTask<>(priorityService, modelMapper, new TypeToken<List<PriorityInfo>>() {}.getType());
    }

    @Override
    protected Task<PriorityInfo> createCreationTask(Map<ReportField, ?> fieldValueMap) {
        return new CreatePriorityTask(priorityService, modelMapper, getRepresentation(), fieldValueMap);
    }

    @Override
    protected Task<PriorityInfo> createUpdateTask(Map<ReportField, ?> fieldValueMap, PriorityInfo selectedComponent) {
        return new UpdatePriorityTask(priorityService, modelMapper, getRepresentation(), fieldValueMap, selectedComponent);
    }

    @Override
    protected Task<List<PriorityInfo>> createDeleteTask(List<PriorityInfo> selectedComponents) {
        List<Long> ids = selectedComponents.stream()
                .map(PriorityInfo::getId)
                .toList();
        return new DeleteTask<>(priorityService, modelMapper, new TypeToken<List<PriorityInfo>>() {}.getType(), ids);
    }

    @Override
    protected Task<Void> createSwapTask(int selectedIndex, int nextIndex) {
        return new SwapTask<>(priorityService, selectedIndex, nextIndex);
    }
}
