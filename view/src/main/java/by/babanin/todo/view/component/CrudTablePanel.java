package by.babanin.todo.view.component;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;

import by.babanin.todo.model.Persistent;
import by.babanin.todo.representation.ReportField;
import by.babanin.todo.task.DeleteTask;
import by.babanin.todo.task.GetTask;
import by.babanin.todo.task.SaveTask;
import by.babanin.todo.task.TaskManager;
import by.babanin.todo.view.component.form.ApplyListener;
import by.babanin.todo.view.component.form.ComponentForm;
import by.babanin.todo.view.component.form.FormRowFactory;
import by.babanin.todo.view.translat.TranslateCode;
import by.babanin.todo.view.translat.Translator;
import by.babanin.todo.view.util.ServiceHolder;

public abstract class CrudTablePanel<C extends Persistent<I>, I> extends JPanel {

    public static final CrudStyle DEFAULT_STYLE = new CrudStyle();

    private final Class<C> componentClass;
    private final CrudStyle crudStyle;

    private TableModel<C> model;
    private JToolBar toolBar;
    private JButton createButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton moveUpButton;
    private JButton moveDownButton;
    private JTable table;
    private FormRowFactory formRowFactory;

    protected CrudTablePanel(Class<C> componentClass, CrudStyle crudStyle) {
        this.componentClass = componentClass;
        this.crudStyle = crudStyle;

        createUiComponents();
        setupTable(table, model);
        addListeners();
        placeComponents();
        actionEnabling();
    }

    protected void createUiComponents() {
        toolBar = new JToolBar();
        model = new TableModel<>(componentClass);
        table = new JTable();

        createButton = new JButton(crudStyle.getCreateButtonIcon());
        editButton = new JButton(crudStyle.getEditButtonIcon());
        deleteButton = new JButton(crudStyle.getDeleteButtonIcon());
        moveUpButton = new JButton(crudStyle.getMoveUpButtonIcon());
        moveDownButton = new JButton(crudStyle.getMoveDownButtonIcon());

        formRowFactory = new FormRowFactory();
    }

    protected void setupTable(JTable table, TableModel<C> model) {
        table.setModel(model);
    }

    protected void addListeners() {
        table.getSelectionModel().addListSelectionListener(event -> actionEnabling());
        createButton.addActionListener(this::showCreationDialog);
        editButton.addActionListener(this::showEditDialog);
        deleteButton.addActionListener(this::showDeleteConfirmDialog);
    }

    protected void placeComponents() {
        addToolBarComponent(createButton);
        addToolBarComponent(editButton);
        addToolBarComponent(deleteButton);
        addToolBarComponent(moveUpButton);
        addToolBarComponent(moveDownButton);

        setLayout(new BorderLayout());
        add(toolBar, BorderLayout.PAGE_START);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    protected void addToolBarComponent(Component component) {
        toolBar.add(component);
    }

    protected void actionEnabling() {
        int selectionCount = table.getSelectionModel().getSelectedItemsCount();
        editButton.setEnabled(selectionCount == 1);
        deleteButton.setEnabled(selectionCount >= 1);
        moveUpButton.setEnabled(selectionCount == 1 && table.getSelectedRow() != 0);
        moveDownButton.setEnabled(selectionCount == 1 && table.getSelectedRow() != table.getRowCount() - 1);
    }

    public void load() {
        GetTask<C, I> task = new GetTask<>(ServiceHolder.getCrudService(componentClass));
        task.addFinishListener(result -> {
            model.addAll(result);
            selectFirstRow();
        });
        TaskManager.run(task);
    }

    protected abstract C createComponent(Map<ReportField, ?> fieldValueMap, C oldComponent);

    private void showCreationDialog(ActionEvent event) {
        ComponentForm<C> form = new ComponentForm<>(componentClass, formRowFactory, crudStyle);
        form.addApplyListener(createCreationListener());
        showComponentForm(form, TranslateCode.CREATION_DIALOG_TITLE);
    }

    private ApplyListener createCreationListener() {
        return fieldValueMap -> {
            C component = createComponent(fieldValueMap, null);
            SaveTask<C, I> saveTask = new SaveTask<>(ServiceHolder.getCrudService(componentClass), component);
            saveTask.addFinishListener(result -> {
                model.add(result);
                selectComponent(component);
            });
            TaskManager.run(saveTask);
        };
    }

    private void showEditDialog(ActionEvent event) {
        C selectedComponent = getSelectedComponent();
        ComponentForm<C> form = new ComponentForm<>(componentClass, formRowFactory, crudStyle, selectedComponent);
        form.addApplyListener(createEditListener(selectedComponent));
        showComponentForm(form, TranslateCode.EDIT_DIALOG_TITLE);
    }

    private ApplyListener createEditListener(C selectedComponent) {
        return fieldValueMap -> {
            C component = createComponent(fieldValueMap, selectedComponent);
            component.setId(selectedComponent.getId());
            SaveTask<C, I> saveTask = new SaveTask<>(ServiceHolder.getCrudService(componentClass), component);
            saveTask.addFinishListener(result -> {
                int row = model.indexOf(selectedComponent);
                model.set(row, result);
                selectRow(row);
            });
            TaskManager.run(saveTask);
        };
    }

    private void showComponentForm(ComponentForm<C> form, String titleCode) {
        Frame frame = JOptionPane.getFrameForComponent(this);
        JDialog dialog = new JDialog(frame, true);
        dialog.setContentPane(form);
        form.setOwner(dialog);
        dialog.setTitle(Translator.toLocale(titleCode).formatted(Translator.getComponentCaption(componentClass)));
        dialog.pack();
        dialog.setLocationRelativeTo(frame);
        dialog.setVisible(true);
    }

    private void showDeleteConfirmDialog(ActionEvent event) {
        Frame frame = JOptionPane.getFrameForComponent(CrudTablePanel.this);
        String componentPluralCaption = Translator.getComponentPluralCaption(componentClass);
        int result = JOptionPane.showConfirmDialog(frame,
                Translator.toLocale(TranslateCode.DELETION_CONFIRM_MESSAGE).formatted(componentPluralCaption.toLowerCase()),
                Translator.toLocale(TranslateCode.DELETE_DIALOG_TITLE).formatted(componentPluralCaption),
                JOptionPane.YES_NO_OPTION);
        if(result == JOptionPane.YES_OPTION) {
            List<C> selectedComponents = getSelectedComponents();
            List<I> ids = selectedComponents.stream()
                    .map(Persistent::getId)
                    .toList();
            DeleteTask<C, I> task = new DeleteTask<>(ServiceHolder.getCrudService(componentClass), ids);
            task.addFinishListener(unused -> model.remove(selectedComponents));
            TaskManager.run(task);
        }
    }

    public C getSelectedComponent() {
        return model.get(table.getSelectedRow());
    }

    public List<C> getSelectedComponents() {
        return model.get(table.getSelectionModel().getSelectedIndices());
    }

    public void selectComponent(C component) {
        int row = model.indexOf(component);
        selectRow(row);
    }

    public void selectRow(int row) {
        table.getSelectionModel().setSelectionInterval(row, row);
    }

    public void selectFirstRow() {
        if(model.getRowCount() > 0) {
            selectRow(0);
        }
    }
}
