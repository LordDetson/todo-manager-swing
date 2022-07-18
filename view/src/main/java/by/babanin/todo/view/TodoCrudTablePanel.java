package by.babanin.todo.view;

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

import by.babanin.todo.model.Priority;
import by.babanin.todo.model.Status;
import by.babanin.todo.model.Todo;
import by.babanin.todo.model.Todo.Fields;
import by.babanin.todo.model.Todo.TodoBuilder;
import by.babanin.todo.representation.ComponentRepresentation;
import by.babanin.todo.representation.ReportField;
import by.babanin.todo.view.component.CrudTablePanel;
import by.babanin.todo.view.component.CustomTableColumnModel;
import by.babanin.todo.view.component.TableModel;
import by.babanin.todo.view.renderer.LocalDataRenderer;
import by.babanin.todo.view.renderer.PriorityRenderer;
import by.babanin.todo.view.renderer.StatusRenderer;
import by.babanin.todo.view.translat.TranslateCode;
import by.babanin.todo.view.translat.Translator;
import by.babanin.todo.view.util.GUIUtils;
import by.babanin.todo.view.util.IconResources;
import by.babanin.todo.view.util.ServiceHolder;

public class TodoCrudTablePanel extends CrudTablePanel<Todo, Long> {

    private JButton priorityButton;

    public TodoCrudTablePanel() {
        super(Todo.class, DEFAULT_STYLE
                .excludeFieldFromCreationForm(Fields.creationDate, Fields.completionDate, Fields.status)
                .excludeFieldFromEditForm(Fields.creationDate, Fields.completionDate, Fields.plannedDate));
    }

    @Override
    protected void createUiComponents() {
        super.createUiComponents();
        priorityButton = new JButton();
        priorityButton.setIcon(IconResources.getIcon("priority"));
    }

    @Override
    protected void setupTable(JTable table, TableModel<Todo> model, CustomTableColumnModel<Todo> columnModel) {
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
        dialog.setSize(GUIUtils.getHalfFrameSize());
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
    protected Todo createComponent(Map<ReportField, ?> fieldValueMap, Todo oldComponent) {
        ComponentRepresentation<Todo> representation = ComponentRepresentation.get(Todo.class);
        TodoBuilder builder = Todo.builder()
                .title((String) fieldValueMap.get(representation.getField(Fields.title)))
                .description((String) fieldValueMap.get(representation.getField(Fields.description)))
                .priority((Priority) fieldValueMap.get(representation.getField(Fields.priority)))
                .status(Status.OPEN)
                .creationDate(LocalDate.now());
        Status status = (Status) fieldValueMap.get(representation.getField(Fields.status));
        if(status != null) {
            builder.status(status);
            if(status == Status.CLOSED) {
                builder.completionDate(LocalDate.now());
            }
        }
        LocalDate plannedDate = (LocalDate) fieldValueMap.get(representation.getField(Fields.plannedDate));
        if(plannedDate == null) {
            plannedDate = oldComponent.getPlannedDate();
        }
        builder.plannedDate(plannedDate);
        return builder.build();
    }
}
