package by.babanin.todo.view;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTable;

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
import by.babanin.todo.view.component.MovableCrudTablePanel;
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

    private JButton priorityButton;

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
        priorityButton = new JButton();
        priorityButton.setIcon(getCrudStyle().getIcon("priority_list"));
    }

    @Override
    protected void setupTable(JTable table, TableModel<Todo> model, CustomTableColumnModel columnModel) {
        super.setupTable(table, model, columnModel);
        table.setDefaultRenderer(Priority.class, new PriorityRenderer());
        table.setDefaultRenderer(Status.class, new StatusRenderer());
        table.setDefaultRenderer(LocalDate.class, new LocalDataRenderer());
    }

    @Override
    protected void addListeners() {
        super.addListeners();
        priorityButton.addActionListener(this::showPriorityDialog);
    }

    private void showPriorityDialog(ActionEvent event) {
        PriorityCrudTablePanel priorityPanel = new PriorityCrudTablePanel();
        priorityPanel.addEditListener(priority -> {
            List<Todo> todos = ServiceHolder.getTodoService().findAllByPriorities(Collections.singleton(priority));
            todos.forEach(todo -> {
                TableModel<Todo> model = getModel();
                int row = getModel().indexOf(todo);
                model.set(row, todo);
            });
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
        dialog.setVisible(true);
    }

    @Override
    protected void placeComponents() {
        super.placeComponents();
        addToolBarComponent(priorityButton);
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
