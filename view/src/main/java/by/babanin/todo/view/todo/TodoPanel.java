package by.babanin.todo.view.todo;

import java.awt.BorderLayout;
import java.util.Objects;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import by.babanin.todo.application.service.TodoService;
import by.babanin.todo.model.Todo;
import by.babanin.todo.model.Todo.Fields;
import by.babanin.todo.representation.ComponentRepresentation;
import by.babanin.todo.view.component.CardPanel;
import by.babanin.todo.view.component.CrudStyle;
import by.babanin.todo.view.component.View;
import by.babanin.todo.view.component.crud.Crud;
import by.babanin.todo.view.component.form.TodoFormRowFactory;
import by.babanin.todo.view.component.validation.TodoValidatorFactory;
import by.babanin.todo.view.util.ServiceHolder;

public class TodoPanel extends JPanel implements View {

    private static final String CRUD_VIEW = "crudTablePanel";
    private static final String TREE_TABLE_VIEW = "treeTablePanel";

    private boolean initialized;
    private JToolBar toolBar;
    private JComboBox<String> comboBox;
    private CardPanel cardPanel;

    public TodoPanel() {
        super(new BorderLayout());
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }

    @Override
    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    @Override
    public void createUiComponents() {
        toolBar = new JToolBar();
        comboBox = new JComboBox<>();
        cardPanel = new CardPanel();
        cardPanel.addView(CRUD_VIEW, () -> new TodoWithDescriptionPanel(createCrud()));
        cardPanel.addView(TREE_TABLE_VIEW, () -> new TodoCrudTreeTablePanel(createCrud()));
    }

    private TodoCrud createCrud() {
        ComponentRepresentation<Todo> representation = ComponentRepresentation.get(Todo.class);
        TodoFormRowFactory formRowFactory = new TodoFormRowFactory();
        CrudStyle crudStyle = new CrudStyle()
                .setValidatorFactory(new TodoValidatorFactory())
                .excludeFieldFromCreationForm(Fields.creationDate, Fields.completionDate, Fields.status)
                .excludeFieldFromEditForm(Fields.creationDate, Fields.completionDate, Fields.plannedDate);
        TodoService todoService = ServiceHolder.getTodoService();
        return new TodoCrud(this, todoService, representation, formRowFactory, crudStyle);
    }

    @Override
    public void addListeners() {
        comboBox.addItemListener(e -> cardPanel.showView((String) e.getItem()));
        cardPanel.addViewChangedListener((viewName, view) -> {
            toolBar.removeAll();
            if(Objects.equals(viewName, CRUD_VIEW)) {
                TodoWithDescriptionPanel panel = (TodoWithDescriptionPanel) view;
                TodoCrudTablePanel crudTablePanel = panel.getCrudTablePanel();
                Crud<Todo, Long, TodoService> crud = crudTablePanel.getCrud();
                toolBar.add(crud.getShowCreationDialogAction());
                toolBar.add(crud.getShowUpdateDialogAction());
                toolBar.add(crud.getShowDeletionConfirmationDialogAction());
                toolBar.add(crudTablePanel.getMoveUpAction());
                toolBar.add(crudTablePanel.getMoveDownAction());
                toolBar.add(crudTablePanel.getShowPrioritiesAction());
                toolBar.addSeparator();
            }
            else if(Objects.equals(viewName, TREE_TABLE_VIEW)) {
                TodoCrudTreeTablePanel panel = (TodoCrudTreeTablePanel) view;
                Crud<Todo, Long, TodoService> crud = panel.getCrud();
                toolBar.add(crud.getShowCreationDialogAction());
                toolBar.add(crud.getShowUpdateDialogAction());
                toolBar.add(crud.getShowDeletionConfirmationDialogAction());
                toolBar.addSeparator();
            }
            toolBar.add(comboBox);
        });
    }

    @Override
    public void placeComponents() {
        add(toolBar, BorderLayout.PAGE_START);
        add(cardPanel, BorderLayout.CENTER);
    }

    @Override
    public void load() {
        comboBox.addItem(CRUD_VIEW);
        comboBox.addItem(TREE_TABLE_VIEW);
    }

    @Override
    public void clear() {
        comboBox.removeAll();
        cardPanel.clear();
    }
}
