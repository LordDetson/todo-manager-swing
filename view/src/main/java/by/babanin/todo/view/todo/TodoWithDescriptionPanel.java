package by.babanin.todo.view.todo;

import java.awt.BorderLayout;
import java.util.function.Predicate;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

import by.babanin.todo.model.Todo;
import by.babanin.todo.model.Todo.Fields;
import by.babanin.todo.view.component.TextAreaPanel;
import by.babanin.todo.view.component.View;
import by.babanin.todo.view.translat.TranslateCode;
import by.babanin.todo.view.translat.Translator;

public class TodoWithDescriptionPanel extends JPanel implements View {

    private final TodoCrud crud;

    private boolean initialized;
    private TodoCrudTablePanel crudTablePanel;
    private TextAreaPanel descriptionPanel;

    public TodoWithDescriptionPanel(TodoCrud crud) {
        super(new BorderLayout());
        this.crud = crud;
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
        crudTablePanel = new TodoCrudTablePanel(crud);
        crudTablePanel.initialize();
        crudTablePanel.excludeField(Fields.description);
        descriptionPanel = new TextAreaPanel();
        descriptionPanel.setEditable(false);
    }

    @Override
    public void addListeners() {
        crudTablePanel.getTable().getSelectionModel().addListSelectionListener(event -> showTodoDescription());
        crud.addUpdateListener(todo -> showTodoDescription());
    }

    @Override
    public void placeComponents() {
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setTopComponent(crudTablePanel);
        splitPane.setBottomComponent(descriptionPanel);

        add(splitPane, BorderLayout.CENTER);
    }

    @Override
    public void load() {
        crudTablePanel.load();
    }

    @Override
    public void clear() {
        crudTablePanel.clear();
    }

    public TodoCrudTablePanel getCrudTablePanel() {
        return crudTablePanel;
    }

    private void showTodoDescription() {
        Todo selectedComponent = crudTablePanel.getSelectedComponent();
        if(selectedComponent != null && isSingleSelection().test(selectedComponent)) {
            descriptionPanel.setText(selectedComponent.getDescription());
        }
        else {
            descriptionPanel.setText(Translator.toLocale(TranslateCode.NO_DESCRIPTION_TO_SHOW));
        }
    }

    private Predicate<Todo> isSingleSelection() {
        return todo -> crudTablePanel.getTable().getSelectedRows().length == 1;
    }
}
