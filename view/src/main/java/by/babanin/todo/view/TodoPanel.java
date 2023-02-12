package by.babanin.todo.view;

import java.awt.BorderLayout;
import java.util.function.Predicate;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import by.babanin.todo.model.Todo;
import by.babanin.todo.view.component.TextAreaPanel;
import by.babanin.todo.view.translat.TranslateCode;
import by.babanin.todo.view.translat.Translator;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public final class TodoPanel extends JPanel {

    private final transient BeanFactory beanFactory;

    private TodoCrudTablePanel crudTablePanel;
    private TextAreaPanel descriptionPanel;

    public TodoPanel(BeanFactory beanFactory) {
        super(new BorderLayout());
        this.beanFactory = beanFactory;
        createUiComponents();
        addListeners();
        placeComponents();
    }

    private void createUiComponents() {
        crudTablePanel = beanFactory.getBean(TodoCrudTablePanel.class);
        descriptionPanel = new TextAreaPanel();
        descriptionPanel.setEditable(false);
    }

    private void addListeners() {
        crudTablePanel.getTable().getSelectionModel().addListSelectionListener(event -> showTodoDescription());
        crudTablePanel.addEditListener(todo -> showTodoDescription());
    }

    private void placeComponents() {
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setTopComponent(crudTablePanel);
        splitPane.setBottomComponent(descriptionPanel);

        add(splitPane, BorderLayout.CENTER);
    }

    public void load() {
        crudTablePanel.load();
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
