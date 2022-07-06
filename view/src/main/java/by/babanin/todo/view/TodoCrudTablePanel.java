package by.babanin.todo.view;

import java.time.LocalDate;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JTable;

import by.babanin.todo.model.Priority;
import by.babanin.todo.model.Status;
import by.babanin.todo.model.Todo;
import by.babanin.todo.view.component.crud.CrudTablePanel;
import by.babanin.todo.view.component.tablemodel.TableModel;
import by.babanin.todo.view.component.tablemodel.TableModelLoader;
import by.babanin.todo.view.renderer.LocalDataRenderer;
import by.babanin.todo.view.renderer.PriorityRenderer;
import by.babanin.todo.view.renderer.StatusRenderer;
import by.babanin.todo.view.translat.TranslateCode;
import by.babanin.todo.view.translat.Translator;
import by.babanin.todo.view.util.GUIUtils;
import by.babanin.todo.view.util.IconResources;
import by.babanin.todo.view.util.ServiceHolder;

public class TodoCrudTablePanel extends CrudTablePanel<Todo, Long> {

    private final JFrame owner;
    private JButton priorityButton;

    public TodoCrudTablePanel(JFrame owner) {
        super(Todo.class, new TableModelLoader<>(ServiceHolder.getTodoService()));
        this.owner = owner;
    }

    @Override
    protected void createComponents() {
        super.createComponents();
        priorityButton = new JButton();
        priorityButton.setIcon(IconResources.getIcon("priority"));
    }

    @Override
    protected void setupTable(JTable table, TableModel<Todo> model) {
        super.setupTable(table, model);
        table.setDefaultRenderer(Priority.class, new PriorityRenderer());
        table.setDefaultRenderer(Status.class, new StatusRenderer());
        table.setDefaultRenderer(LocalDate.class, new LocalDataRenderer());
    }

    @Override
    protected void addListeners() {
        super.addListeners();
        priorityButton.addActionListener(event -> {
            PriorityCrudTablePanel priorityPanel = new PriorityCrudTablePanel();
            priorityPanel.load();

            JDialog dialog = new JDialog(owner, true);
            dialog.setContentPane(priorityPanel);
            dialog.setSize(GUIUtils.getHalfFrameSize());
            dialog.setLocationRelativeTo(null);
            dialog.setTitle(Translator.toLocale(TranslateCode.PRIORITY_FRAME_TITLE));
            dialog.setVisible(true);
        });
    }

    @Override
    protected void placeComponents() {
        super.placeComponents();
        addToolBarComponent(priorityButton);
    }
}
