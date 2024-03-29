package by.babanin.todo.ui;

import java.awt.BorderLayout;
import java.util.function.Predicate;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

import org.springframework.stereotype.Component;

import by.babanin.ext.component.TextAreaPanel;
import by.babanin.ext.message.Translator;
import by.babanin.ext.preference.PreferenceAware;
import by.babanin.ext.preference.PreferencesGroup;
import by.babanin.ext.preference.SplitPanePreference;
import by.babanin.todo.ui.dto.ToDoInfo;
import by.babanin.todo.ui.translat.AppTranslateCode;

@Component
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
                .map(ToDoInfo::getDescription)
                .orElse(Translator.toLocale(AppTranslateCode.NO_DESCRIPTION_TO_SHOW));
        descriptionPanel.setText(text);
    }

    private Predicate<ToDoInfo> isSingleSelection() {
        return todo -> crudTablePanel.getTable().getSelectedRows().length == 1;
    }

    @Override
    public void apply(PreferencesGroup preferencesGroup) {
        preferencesGroup.getOpt(SPLIT_PANE_KEY)
                .ifPresent(preference -> splitPane.setDividerLocation(((SplitPanePreference) preference).getProportion()));
        preferencesGroup.getOpt(TODO_CRUD_TABLE_PANEL_KEY)
                .ifPresent(preference -> crudTablePanel.apply((PreferencesGroup) preference));
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
