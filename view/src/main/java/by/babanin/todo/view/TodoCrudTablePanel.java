package by.babanin.todo.view;

import java.awt.event.KeyEvent;
import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JTable;

import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import by.babanin.todo.application.service.PriorityService;
import by.babanin.todo.application.service.TodoService;
import by.babanin.todo.application.status.StatusWorkflow;
import by.babanin.todo.model.Priority;
import by.babanin.todo.model.Status;
import by.babanin.todo.model.Todo;
import by.babanin.todo.model.Todo.Fields;
import by.babanin.todo.representation.ReportField;
import by.babanin.todo.task.Task;
import by.babanin.todo.task.todo.CreateTodoTask;
import by.babanin.todo.task.todo.UpdateTodoTask;
import by.babanin.todo.view.component.CrudStyle;
import by.babanin.todo.view.component.CustomTableColumnModel;
import by.babanin.todo.view.component.IndexableTableModel;
import by.babanin.todo.view.component.MovableCrudTablePanel;
import by.babanin.todo.view.component.TableModel;
import by.babanin.todo.view.component.ToolAction;
import by.babanin.todo.view.component.form.TodoFormRowFactory;
import by.babanin.todo.view.component.validation.TodoValidatorFactory;
import by.babanin.todo.view.renderer.LocalDataRenderer;
import by.babanin.todo.view.renderer.PriorityRenderer;
import by.babanin.todo.view.renderer.StatusRenderer;
import by.babanin.todo.view.translat.TranslateCode;
import by.babanin.todo.view.translat.Translator;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public abstract class TodoCrudTablePanel extends MovableCrudTablePanel<Todo, Long> {

    private JButton showPrioritiesButton;

    protected TodoCrudTablePanel(TodoService todoService, PriorityService priorityService) {
        super(todoService, Todo.class, new TodoFormRowFactory(priorityService), new CrudStyle()
                .setValidatorFactory(new TodoValidatorFactory())
                .excludeFieldFromCreationForm(Fields.creationDate, Fields.completionDate, Fields.status)
                .excludeFieldFromEditForm(Fields.creationDate, Fields.completionDate, Fields.plannedDate)
                .excludeFieldFromTable(Fields.description));
        setName("todoCrudTablePanel");
    }

    @Override
    protected void createUiComponents() {
        super.createUiComponents();
        Action showPrioritiesAction = new ToolAction(
                getCrudStyle().getIcon("priority_list"),
                Translator.toLocale(TranslateCode.TOOLTIP_BUTTON_SHOW_PRIORITIES),
                KeyEvent.VK_P,
                this::showPriorityDialog
        );
        showPrioritiesButton = new JButton(showPrioritiesAction);
    }

    @Override
    protected void setupTable(JTable table, TableModel<Todo> model, CustomTableColumnModel columnModel) {
        super.setupTable(table, model, columnModel);
        table.setDefaultRenderer(Priority.class, new PriorityRenderer());
        table.setDefaultRenderer(Status.class, new StatusRenderer());
        table.setDefaultRenderer(LocalDate.class, new LocalDataRenderer());
    }

    @Override
    protected void handleCreation(Todo result) {
        getModel().add((int) result.getPosition(), result);
        selectComponent(result);
    }

    private void showPriorityDialog() {
        PrioritiesDialog dialog = createPrioritiesDialog(this);
        PriorityCrudTablePanel priorityPanel = dialog.getContentPane();
        priorityPanel.addEditListener(priority -> {
            int columnIndex = getTable().getColumnModel().getColumnIndex(Fields.priority);
            IndexableTableModel<Todo> model = getModel();
            model.getAll().stream()
                    .filter(todo -> Objects.nonNull(todo.getPriority()))
                    .filter(todo -> priority.getId().equals(todo.getPriority().getId()))
                    .peek(todo -> todo.setPriority(priority))
                    .map(model::indexOf)
                    .forEach(row -> model.fireTableCellUpdated(row, columnIndex));
        });
        priorityPanel.addDeletionListener(priorities -> {
            int columnIndex = getTable().getColumnModel().getColumnIndex(Fields.priority);
            TableModel<Todo> model = getModel();
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
        addToolBarComponent(showPrioritiesButton);
    }

    @Override
    protected boolean canEdit() {
        boolean canEdit = super.canEdit();
        if(canEdit) {
            canEdit = getSelectedComponent()
                    .map(todo -> !StatusWorkflow.get(todo).isFinalStatus())
                    .orElse(false);
        }
        return canEdit;
    }

    @Override
    protected TodoService getService() {
        return (TodoService) super.getService();
    }

    @Override
    protected Task<Todo> createCreationTask(Map<ReportField, ?> fieldValueMap) {
        return new CreateTodoTask(getService(), getRepresentation(), fieldValueMap);
    }

    @Override
    protected Task<Todo> createUpdateTask(Map<ReportField, ?> fieldValueMap, Todo selectedComponent) {
        return new UpdateTodoTask(getService(), getRepresentation(), fieldValueMap, selectedComponent);
    }
}
