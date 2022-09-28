package by.babanin.todo.view;

import java.awt.BorderLayout;
import java.util.function.Predicate;

import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;

import by.babanin.todo.model.Todo;
import by.babanin.todo.view.component.TextAreaPanel;
import by.babanin.todo.view.component.View;
import by.babanin.todo.view.translat.TranslateCode;
import by.babanin.todo.view.translat.Translator;

public class TodoPanel extends JPanel implements View {

    private JToolBar toolBar;
    private TodoCrudTablePanel crudTablePanel;
    private TextAreaPanel descriptionPanel;

    public TodoPanel() {
        super(new BorderLayout());
        initialize();
    }

    @Override
    public void createUiComponents() {
        crudTablePanel = new TodoCrudTablePanel();
        descriptionPanel = new TextAreaPanel();
        descriptionPanel.setEditable(false);

        toolBar = new JToolBar();
        toolBar.add(crudTablePanel.getShowCreationDialogAction());
        toolBar.add(crudTablePanel.getShowEditDialogAction());
        toolBar.add(crudTablePanel.getShowDeleteConfirmDialogAction());
        toolBar.add(crudTablePanel.getMoveUpAction());
        toolBar.add(crudTablePanel.getMoveDownAction());
        toolBar.add(crudTablePanel.getShowPrioritiesAction());
    }

    @Override
    public void addListeners() {
        crudTablePanel.getTable().getSelectionModel().addListSelectionListener(event -> showTodoDescription());
        crudTablePanel.addEditListener(todo -> showTodoDescription());
    }

    @Override
    public void placeComponents() {
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setTopComponent(crudTablePanel);
        splitPane.setBottomComponent(descriptionPanel);

        add(toolBar, BorderLayout.PAGE_START);
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

    private void showTodoDescription() {
        String text = crudTablePanel.getSelectedComponent()
                .filter(isSingleSelection())
                .map(Todo::getDescription)
                .orElse(Translator.toLocale(TranslateCode.NO_DESCRIPTION_TO_SHOW));
        descriptionPanel.setText(text);
    }

    private Predicate<Todo> isSingleSelection() {
        return todo -> crudTablePanel.getTable().getSelectedRows().length == 1;
    }
}
