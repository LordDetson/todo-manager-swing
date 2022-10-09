package by.babanin.todo.view.todo;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.time.LocalDate;
import java.util.Objects;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;

import by.babanin.todo.application.service.PriorityService;
import by.babanin.todo.application.service.TodoService;
import by.babanin.todo.application.status.StatusWorkflow;
import by.babanin.todo.model.Priority;
import by.babanin.todo.model.Status;
import by.babanin.todo.model.Todo;
import by.babanin.todo.model.Todo.Fields;
import by.babanin.todo.representation.ComponentRepresentation;
import by.babanin.todo.view.component.CrudStyle;
import by.babanin.todo.view.component.IndexableTableModel;
import by.babanin.todo.view.component.MovableCrudTablePanel;
import by.babanin.todo.view.component.RunnableAction;
import by.babanin.todo.view.component.form.PriorityFormRowFactory;
import by.babanin.todo.view.component.validation.PriorityValidatorFactory;
import by.babanin.todo.view.priority.PriorityCrud;
import by.babanin.todo.view.priority.PriorityPanel;
import by.babanin.todo.view.renderer.LocalDataRenderer;
import by.babanin.todo.view.renderer.PriorityRenderer;
import by.babanin.todo.view.renderer.StatusRenderer;
import by.babanin.todo.view.translat.TranslateCode;
import by.babanin.todo.view.translat.Translator;
import by.babanin.todo.view.util.GUIUtils;
import by.babanin.todo.view.util.ServiceHolder;

public class TodoCrudTablePanel extends MovableCrudTablePanel<Todo, Long, TodoService, JTable, IndexableTableModel<Todo>> {

    private static final String PRIORITIES_DIALOG_CLOSING_ACTION_KEY = "closePrioritiesDialog";

    private transient Action showPrioritiesAction;

    public TodoCrudTablePanel(TodoCrud crud) {
        super(crud);
    }

    @Override
    public void createUiComponents() {
        super.createUiComponents();
        showPrioritiesAction = new RunnableAction(
                getCrud().getCrudStyle().getIcon("priority_list"),
                Translator.toLocale(TranslateCode.TOOLTIP_BUTTON_SHOW_PRIORITIES),
                KeyEvent.VK_P,
                this::showPriorityDialog
        );
    }

    @Override
    protected IndexableTableModel<Todo> createTableModel(ComponentRepresentation<Todo> representation) {
        return new IndexableTableModel<>(representation);
    }

    @Override
    protected JTable createTable(IndexableTableModel<Todo> model) {
        JTable table = new JTable(model);
        table.setDefaultRenderer(Priority.class, new PriorityRenderer());
        table.setDefaultRenderer(Status.class, new StatusRenderer());
        table.setDefaultRenderer(LocalDate.class, new LocalDataRenderer());
        return table;
    }

    @Override
    public void addListeners() {
        super.addListeners();
        getCrud().setCanUpdateFunction(todo -> !StatusWorkflow.get(todo).isFinalStatus());
    }

    @Override
    protected void handleCreation(Todo result) {
        getModel().add((int) result.getPosition(), result);
        selectComponent(result);
    }

    private void showPriorityDialog() {
        PriorityService priorityService = ServiceHolder.getPriorityService();
        ComponentRepresentation<Priority> representation = ComponentRepresentation.get(Priority.class);
        PriorityCrud crud = new PriorityCrud(this, priorityService, representation, new PriorityFormRowFactory(), new CrudStyle()
                .setValidatorFactory(new PriorityValidatorFactory()));
        PriorityPanel priorityPanel = new PriorityPanel(crud);
        priorityPanel.initialize();
        priorityPanel.getCrud().addUpdateListener(priority -> {
            int columnIndex = getTable().getColumnModel().getColumnIndex(Fields.priority);
            IndexableTableModel<Todo> model = getModel();
            model.getAll().stream()
                    .filter(todo -> Objects.nonNull(todo.getPriority()))
                    .filter(todo -> priority.getId().equals(todo.getPriority().getId()))
                    .peek(todo -> todo.setPriority(priority))
                    .map(model::indexOf)
                    .forEach(row -> model.fireTableCellUpdated(row, columnIndex));
        });
        priorityPanel.getCrud().addDeletionListener(priorities -> {
            int columnIndex = getTable().getColumnModel().getColumnIndex(Fields.priority);
            IndexableTableModel<Todo> model = getModel();
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

    public Action getShowPrioritiesAction() {
        return showPrioritiesAction;
    }
}
