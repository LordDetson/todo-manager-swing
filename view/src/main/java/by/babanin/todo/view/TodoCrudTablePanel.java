package by.babanin.todo.view;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;

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
import by.babanin.todo.view.component.RunnableAction;
import by.babanin.todo.view.component.TableModel;
import by.babanin.todo.view.component.form.TodoFormRowFactory;
import by.babanin.todo.view.component.validation.TodoValidatorFactory;
import by.babanin.todo.view.renderer.LocalDataRenderer;
import by.babanin.todo.view.renderer.PriorityRenderer;
import by.babanin.todo.view.renderer.StatusRenderer;
import by.babanin.todo.view.translat.TranslateCode;
import by.babanin.todo.view.translat.Translator;
import by.babanin.todo.view.util.GUIUtils;
import by.babanin.todo.view.util.ServiceHolder;

public class TodoCrudTablePanel extends MovableCrudTablePanel<Todo, Long> {

    private static final String PRIORITIES_DIALOG_CLOSING_ACTION_KEY = "closePrioritiesDialog";
    private JButton showPrioritiesButton;

    public TodoCrudTablePanel() {
        super(Todo.class, new TodoFormRowFactory(), new CrudStyle()
                .setValidatorFactory(new TodoValidatorFactory())
                .excludeFieldFromCreationForm(Fields.creationDate, Fields.completionDate, Fields.status)
                .excludeFieldFromEditForm(Fields.creationDate, Fields.completionDate, Fields.plannedDate)
                .excludeFieldFromTable(Fields.description));
    }

    @Override
    protected void createUiComponents() {
        super.createUiComponents();
        Action showPrioritiesAction = new RunnableAction(
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
        PriorityCrudTablePanel priorityPanel = new PriorityCrudTablePanel();
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

        Frame frame = JOptionPane.getFrameForComponent(this);
        JDialog dialog = new JDialog(frame, true);
        dialog.setContentPane(priorityPanel);
        Dimension smallFrameSize = GUIUtils.getSmallFrameSize();
        dialog.setMinimumSize(smallFrameSize);
        dialog.setSize(smallFrameSize);
        dialog.setLocationRelativeTo(frame);
        dialog.setTitle(Translator.toLocale(TranslateCode.PRIORITY_FRAME_TITLE));
        addPrioritiesDialogClosingAction(dialog);
        dialog.setVisible(true);
    }

    private static void addPrioritiesDialogClosingAction(JDialog dialog) {
        KeyStroke escapeStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        GUIUtils.addDialogKeyAction(dialog, escapeStroke, PRIORITIES_DIALOG_CLOSING_ACTION_KEY, new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });
    }

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
    protected Task<Todo> createCreationTask(Map<ReportField, ?> fieldValueMap) {
        TodoService service = ServiceHolder.getTodoService();
        return new CreateTodoTask(service, getRepresentation(), fieldValueMap);
    }

    @Override
    protected Task<Todo> createUpdateTask(Map<ReportField, ?> fieldValueMap, Todo selectedComponent) {
        TodoService service = ServiceHolder.getTodoService();
        return new UpdateTodoTask(service, getRepresentation(), fieldValueMap, selectedComponent);
    }
}
