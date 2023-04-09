package by.babanin.todo.view;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.util.function.Predicate;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import by.babanin.todo.model.Todo;
import by.babanin.todo.preferences.PreferenceAware;
import by.babanin.todo.preferences.PreferencesGroup;
import by.babanin.todo.view.component.TextAreaPanel;
import by.babanin.todo.view.preference.SplitPanePreference;
import by.babanin.todo.view.translat.TranslateCode;
import by.babanin.todo.view.translat.Translator;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public final class TodoPanel extends JPanel implements PreferenceAware<PreferencesGroup> {

    private static final String SPLIT_PANE_KEY = "splitPane";
    private static final String TODO_CRUD_TABLE_PANEL_KEY = "todoCrudTablePanel";
    private static final double DEFAULT_PROPORTION = 0.6;

    private final TodoCrudTablePanel crudTablePanel;
    private TextAreaPanel descriptionPanel;
    private JSplitPane splitPane;

    public TodoPanel(TodoCrudTablePanel crudTablePanel) {
        super(new BorderLayout());
        this.crudTablePanel = crudTablePanel;
        setName("todoPanel");
        createUiComponents();
        addListeners();
        placeComponents();
    }

    private void createUiComponents() {
        descriptionPanel = new TextAreaPanel();
        descriptionPanel.setEditable(false);

        splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setTopComponent(crudTablePanel);
        splitPane.setBottomComponent(descriptionPanel);
    }

    private void addListeners() {
        crudTablePanel.getTable().getSelectionModel().addListSelectionListener(event -> showTodoDescription());
        crudTablePanel.addEditListener(todo -> showTodoDescription());
    }

    private void placeComponents() {
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

    @Override
    public void apply(PreferencesGroup preferencesGroup) {
        EventQueue.invokeLater(() -> {
            preferencesGroup.get(SPLIT_PANE_KEY)
                    .ifPresent(preference -> splitPane.setDividerLocation(((SplitPanePreference) preference).getProportion()));
            preferencesGroup.get(TODO_CRUD_TABLE_PANEL_KEY)
                    .ifPresent(preference -> crudTablePanel.apply((PreferencesGroup) preference));
        });
    }

    @Override
    public PreferencesGroup createCurrentPreference() {
        SplitPanePreference splitPanePreference = new SplitPanePreference();
        splitPanePreference.storeProportion(splitPane);
        PreferencesGroup preferencesGroup = new PreferencesGroup();
        preferencesGroup.put(SPLIT_PANE_KEY, splitPanePreference);
        preferencesGroup.put(TODO_CRUD_TABLE_PANEL_KEY, crudTablePanel.createCurrentPreference());
        return preferencesGroup;
    }

    @Override
    public PreferencesGroup createDefaultPreference() {
        SplitPanePreference splitPanePreference = new SplitPanePreference();
        splitPanePreference.setProportion(DEFAULT_PROPORTION);
        PreferencesGroup preferencesGroup = new PreferencesGroup();
        preferencesGroup.put(SPLIT_PANE_KEY, splitPanePreference);
        preferencesGroup.put(TODO_CRUD_TABLE_PANEL_KEY, crudTablePanel.createDefaultPreference());
        return preferencesGroup;
    }

    @Override
    public String getKey() {
        return getName();
    }
}
