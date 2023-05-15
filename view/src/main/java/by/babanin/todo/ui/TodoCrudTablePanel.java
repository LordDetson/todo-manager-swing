package by.babanin.todo.ui;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.swing.JButton;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.KeyStroke;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import by.babanin.ext.component.CrudStyle;
import by.babanin.ext.component.CustomTableColumnModel;
import by.babanin.ext.component.IndexableTableModel;
import by.babanin.ext.component.MovableCrudTablePanel;
import by.babanin.ext.component.TableModel;
import by.babanin.ext.component.action.Action;
import by.babanin.ext.component.action.BindingAction;
import by.babanin.ext.component.table.renderer.LocalDataRenderer;
import by.babanin.ext.component.util.IconRegister;
import by.babanin.ext.message.Translator;
import by.babanin.ext.representation.ReportField;
import by.babanin.ext.task.Task;
import by.babanin.todo.application.service.PriorityService;
import by.babanin.todo.application.service.TodoService;
import by.babanin.todo.application.status.StatusWorkflow;
import by.babanin.todo.model.Status;
import by.babanin.todo.model.Todo;
import by.babanin.todo.model.Todo.Fields;
import by.babanin.todo.task.DeleteTask;
import by.babanin.todo.task.GetTask;
import by.babanin.todo.task.SwapTask;
import by.babanin.todo.task.todo.CreateTodoTask;
import by.babanin.todo.task.todo.UpdateTodoTask;
import by.babanin.todo.ui.form.TodoFormRowFactory;
import by.babanin.todo.ui.validation.TodoValidatorFactory;
import by.babanin.todo.ui.dto.PriorityInfo;
import by.babanin.todo.ui.dto.ToDoInfo;
import by.babanin.todo.ui.renderer.PriorityRenderer;
import by.babanin.todo.ui.renderer.StatusRenderer;
import by.babanin.todo.ui.translat.AppTranslateCode;

@Component
@DependsOn("iconProvider")
public abstract class TodoCrudTablePanel extends MovableCrudTablePanel<ToDoInfo> {

    private final TodoService todoService;
    private final ModelMapper modelMapper;
    private Action showPrioritiesDialogAction;

    protected TodoCrudTablePanel(TodoService todoService, PriorityService priorityService, ModelMapper modelMapper) {
        super(ToDoInfo.class, new TodoFormRowFactory(priorityService, modelMapper), new CrudStyle()
                .setValidatorFactory(new TodoValidatorFactory())
                .excludeFieldFromCreationForm(Fields.creationDate, Fields.completionDate, Fields.status)
                .excludeFieldFromEditForm(Fields.creationDate, Fields.completionDate, Fields.plannedDate)
                .excludeFieldFromTable(Fields.description));
        this.todoService = todoService;
        this.modelMapper = modelMapper;
        setName("todoCrudTablePanel");
    }

    @Override
    protected void createUiComponents() {
        super.createUiComponents();
        CrudStyle crudStyle = getCrudStyle();
        showPrioritiesDialogAction = Action.builder(() -> new BindingAction(getTable()))
                .id("showPrioritiesDialog")
                .name(Translator.toLocale(AppTranslateCode.TOOLTIP_BUTTON_SHOW_PRIORITIES))
                .toolTip(Translator.toLocale(AppTranslateCode.TOOLTIP_BUTTON_SHOW_PRIORITIES))
                .smallIcon(IconRegister.get("priority_list", crudStyle.getSmallIconSize()))
                .largeIcon(IconRegister.get("priority_list", crudStyle.getLargeIconSize()))
                .accelerator(KeyStroke.getKeyStroke("control P"))
                .action(actionEvent -> showPriorityDialog())
                .build();
    }

    @Override
    protected void setupTable(JTable table, TableModel<ToDoInfo> model, CustomTableColumnModel columnModel, JPopupMenu popupMenu) {
        super.setupTable(table, model, columnModel, popupMenu);
        table.setDefaultRenderer(PriorityInfo.class, new PriorityRenderer());
        table.setDefaultRenderer(Status.class, new StatusRenderer());
        table.setDefaultRenderer(LocalDate.class, new LocalDataRenderer());
        setDefaultColumnIdsToFit(Fields.title);
    }

    @Override
    protected void handleCreation(ToDoInfo result) {
        getModel().add((int) result.getPosition(), result);
        selectComponent(result);
    }

    private void showPriorityDialog() {
        PrioritiesDialog dialog = createPrioritiesDialog(this);
        PriorityCrudTablePanel priorityPanel = dialog.getContentPane();
        priorityPanel.addEditListener(priority -> {
            int columnIndex = getTable().getColumnModel().getColumnIndex(Fields.priority);
            IndexableTableModel<ToDoInfo> model = getModel();
            model.getAll().stream()
                    .filter(todo -> Objects.nonNull(todo.getPriority()))
                    .filter(todo -> priority.getId().equals(todo.getPriority().getId()))
                    .peek(todo -> todo.setPriority(priority))
                    .map(model::indexOf)
                    .forEach(row -> model.fireTableCellUpdated(row, columnIndex));
        });
        priorityPanel.addDeletionListener(priorities -> {
            int columnIndex = getTable().getColumnModel().getColumnIndex(Fields.priority);
            TableModel<ToDoInfo> model = getModel();
            model.getAll().stream()
                    .filter(todo -> priorities.contains(todo.getPriority()))
                    .peek(todo -> todo.setPriority(null))
                    .map(model::indexOf)
                    .forEach(row -> model.fireTableCellUpdated(row, columnIndex));
        });
        priorityPanel.load();
        dialog.setVisible(true);
    }

    @Lookup
    protected abstract PrioritiesDialog createPrioritiesDialog(java.awt.Component component);

    @Override
    protected void placeComponents() {
        super.placeComponents();
        addToolBarComponent(new JButton(showPrioritiesDialogAction));
    }

    @Override
    protected boolean canEdit() {
        boolean canEdit = super.canEdit();
        if(canEdit) {
            canEdit = getSelectedComponent()
                    .map(todo -> !StatusWorkflow.get(modelMapper.map(todo, Todo.class)).isFinalStatus())
                    .orElse(false);
        }
        return canEdit;
    }

    @Override
    protected Task<List<ToDoInfo>> createLoadTask() {
        return new GetTask<>(todoService, modelMapper, new TypeToken<List<ToDoInfo>>() {}.getType());
    }

    @Override
    protected Task<ToDoInfo> createCreationTask(Map<ReportField, ?> fieldValueMap) {
        return new CreateTodoTask(todoService, modelMapper, getRepresentation(), fieldValueMap);
    }

    @Override
    protected Task<ToDoInfo> createUpdateTask(Map<ReportField, ?> fieldValueMap, ToDoInfo selectedComponent) {
        return new UpdateTodoTask(todoService, modelMapper, getRepresentation(), fieldValueMap, selectedComponent);
    }

    @Override
    protected Task<List<ToDoInfo>> createDeleteTask(List<ToDoInfo> selectedComponents) {
        List<Long> ids = selectedComponents.stream()
                .map(ToDoInfo::getId)
                .toList();
        return new DeleteTask<>(todoService, modelMapper, new TypeToken<List<ToDoInfo>>() {}.getType(), ids);
    }

    @Override
    protected Task<Void> createSwapTask(int selectedIndex, int nextIndex) {
        return new SwapTask<>(todoService, selectedIndex, nextIndex);
    }
}
