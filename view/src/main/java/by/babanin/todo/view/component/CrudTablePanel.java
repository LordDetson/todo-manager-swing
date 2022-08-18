package by.babanin.todo.view.component;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;

import by.babanin.todo.application.service.CrudService;
import by.babanin.todo.model.Persistent;
import by.babanin.todo.representation.ComponentRepresentation;
import by.babanin.todo.representation.ReportField;
import by.babanin.todo.task.DeleteTask;
import by.babanin.todo.task.ExceptionListener;
import by.babanin.todo.task.FinishListener;
import by.babanin.todo.task.GetTask;
import by.babanin.todo.task.Task;
import by.babanin.todo.task.TaskManager;
import by.babanin.todo.view.component.form.ComponentForm;
import by.babanin.todo.view.component.form.FormRowFactory;
import by.babanin.todo.view.exception.ViewException;
import by.babanin.todo.view.progress.ProgressDialog;
import by.babanin.todo.view.translat.TranslateCode;
import by.babanin.todo.view.translat.Translator;
import by.babanin.todo.view.util.ServiceHolder;

public abstract class CrudTablePanel<C extends Persistent<I>, I> extends JPanel {

    private final Map<CrudAction, List<FinishListener<?>>> crudListenersMap = new EnumMap<>(CrudAction.class);
    private final List<ExceptionListener> exceptionListeners = new ArrayList<>();
    private final transient ComponentRepresentation<C> representation;
    private final transient FormRowFactory formRowFactory;
    private final CrudStyle crudStyle;

    private JToolBar toolBar;

    private Action showEditDialogAction;
    private Action showDeleteConfirmDialogAction;
    private JButton createButton;
    private JButton editButton;
    private JButton deleteButton;
    private TableModel<C> model;
    private CustomTableColumnModel columnModel;
    private JTable table;

    protected CrudTablePanel(Class<C> componentClass, FormRowFactory formRowFactory, CrudStyle crudStyle) {
        this.representation = ComponentRepresentation.get(componentClass);
        this.formRowFactory = formRowFactory;
        this.crudStyle = crudStyle;

        createUiComponents();
        setupTable(table, model, columnModel);
        addListeners();
        placeComponents();
        actionEnabling();
    }

    protected void createUiComponents() {
        toolBar = new JToolBar();
        List<String> excludedFieldFromTable = crudStyle.getExcludedFieldFromTable();
        List<ReportField> reportFields = representation.getFields().stream()
                .filter(field -> !excludedFieldFromTable.contains(field.getName()))
                .toList();
        model = createTableModel(representation, reportFields);
        columnModel = new CustomTableColumnModel(reportFields);
        table = new JTable();

        Action showCreationDialogAction = new RunnableAction(
                crudStyle.getCreateButtonIcon(),
                crudStyle.getCreateButtonToolTip(),
                KeyEvent.VK_C,
                this::showCreationDialog
        );
        showEditDialogAction = new RunnableAction(
                crudStyle.getEditButtonIcon(),
                crudStyle.getEditButtonToolTip(),
                KeyEvent.VK_E,
                this::showEditDialog
        );
        showDeleteConfirmDialogAction = new RunnableAction(
                crudStyle.getDeleteButtonIcon(),
                crudStyle.getDeleteButtonToolTip(),
                KeyEvent.VK_D,
                this::showDeleteConfirmDialog
        );

        createButton = new JButton(showCreationDialogAction);
        editButton = new JButton(showEditDialogAction);
        deleteButton = new JButton(showDeleteConfirmDialogAction);
    }

    protected TableModel<C> createTableModel(ComponentRepresentation<C> representation, List<ReportField> fields) {
        return new TableModel<>(representation, fields);
    }

    protected void setupTable(JTable table, TableModel<C> model, CustomTableColumnModel columnModel) {
        table.setModel(model);
        table.setColumnModel(columnModel);
    }

    protected void addListeners() {
        table.getSelectionModel().addListSelectionListener(event -> actionEnabling());

        addCreationListener(result -> {
            model.add(result);
            selectComponent(result);
        });
        addLoadListener(result -> {
            model.addAll(result);
            selectFirstRow();
        });
        addEditListener(result -> {
            int row = table.getSelectedRow();
            model.set(row, result);
            selectRow(row);
        });
        addDeletionListener(result -> model.remove(result));
        addExceptionListener(exception -> {
            clear();
            load();
        });
    }

    public void addCreationListener(FinishListener<C> listener) {
        addCrudListener(CrudAction.CREATE, listener);
    }

    public void addLoadListener(FinishListener<List<C>> listener) {
        addCrudListener(CrudAction.READ, listener);
    }

    public void addEditListener(FinishListener<C> listener) {
        addCrudListener(CrudAction.UPDATE, listener);
    }

    public void addDeletionListener(FinishListener<List<C>> listener) {
        addCrudListener(CrudAction.DELETE, listener);
    }

    private void addCrudListener(CrudAction crudAction, FinishListener<?> listener) {
        crudListenersMap.computeIfAbsent(crudAction, action -> new ArrayList<>());
        crudListenersMap.get(crudAction).add(listener);
    }

    public void addExceptionListener(ExceptionListener exceptionListener) {
        exceptionListeners.add(exceptionListener);
    }

    protected void placeComponents() {
        addToolBarComponent(createButton);
        addToolBarComponent(editButton);
        addToolBarComponent(deleteButton);

        setLayout(new BorderLayout());
        add(toolBar, BorderLayout.PAGE_START);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    protected void addToolBarComponent(Component component) {
        toolBar.add(component);
    }

    protected void actionEnabling() {
        showEditDialogAction.setEnabled(canEdit());
        showDeleteConfirmDialogAction.setEnabled(canDelete());
    }

    protected boolean canEdit() {
        int selectionCount = table.getSelectionModel().getSelectedItemsCount();
        return selectionCount == 1;
    }

    protected boolean canDelete() {
        int selectionCount = table.getSelectionModel().getSelectedItemsCount();
        return selectionCount >= 1;
    }

    @SuppressWarnings("unchecked")
    public void load() {
        GetTask<C, I, CrudService<C, I>> task = new GetTask<>(ServiceHolder.getCrudService(representation.getComponentClass()));
        if(crudListenersMap.containsKey(CrudAction.READ)) {
            crudListenersMap.get(CrudAction.READ)
                    .forEach(finishListener -> task.addFinishListener((FinishListener<List<C>>) finishListener));
        }
        task.addFinishListener(components -> actionEnabling());
        exceptionListeners.forEach(task::addExceptionListener);
        ProgressDialog.initiate(task);
        TaskManager.run(task);
    }

    public void clear() {
        table.clearSelection();
        model.clear();
    }

    private void showCreationDialog() {
        ComponentForm<C> form = new ComponentForm<>(representation.getComponentClass(), formRowFactory, crudStyle);
        form.addApplyListener(this::runCreationTask);
        showComponentForm(form, TranslateCode.CREATION_DIALOG_TITLE);
    }

    @SuppressWarnings("unchecked")
    private void runCreationTask(Map<ReportField, ?> fieldValueMap) {
        Task<C> task = createCreationTask(fieldValueMap);
        if(crudListenersMap.containsKey(CrudAction.CREATE)) {
            crudListenersMap.get(CrudAction.CREATE)
                    .forEach(finishListener -> task.addFinishListener((FinishListener<C>) finishListener));
        }
        task.addFinishListener(component -> actionEnabling());
        exceptionListeners.forEach(task::addExceptionListener);
        ProgressDialog.initiate(task);
        TaskManager.run(task);
    }

    protected abstract Task<C> createCreationTask(Map<ReportField, ?> fieldValueMap);

    private void showEditDialog() {
        C selectedComponent = getSelectedComponent()
                .orElseThrow(() -> new ViewException("Can't open edit dialog because component is not selected"));
        ComponentForm<C> form = new ComponentForm<>(representation.getComponentClass(), formRowFactory, crudStyle, selectedComponent);
        form.addApplyListener(fieldValueMap -> runUpdateTask(fieldValueMap, selectedComponent));
        showComponentForm(form, TranslateCode.EDIT_DIALOG_TITLE);
    }

    @SuppressWarnings("unchecked")
    private void runUpdateTask(Map<ReportField, ?> fieldValueMap, C selectedComponent) {
        Task<C> task = createUpdateTask(fieldValueMap, selectedComponent);
        if(crudListenersMap.containsKey(CrudAction.UPDATE)) {
            crudListenersMap.get(CrudAction.UPDATE)
                    .forEach(finishListener -> task.addFinishListener((FinishListener<C>) finishListener));
        }
        task.addFinishListener(component -> actionEnabling());
        exceptionListeners.forEach(task::addExceptionListener);
        ProgressDialog.initiate(task);
        TaskManager.run(task);
    }

    protected abstract Task<C> createUpdateTask(Map<ReportField, ?> fieldValueMap, C selectedComponent);

    private void showComponentForm(ComponentForm<C> form, String titleCode) {
        Frame frame = JOptionPane.getFrameForComponent(this);
        JDialog dialog = new JDialog(frame, true);
        dialog.setContentPane(form);
        form.setOwner(dialog);
        dialog.setTitle(Translator.toLocale(titleCode).formatted(Translator.getComponentCaption(representation.getComponentClass())));
        dialog.pack();
        dialog.setLocationRelativeTo(frame);
        dialog.setMinimumSize(dialog.getSize());
        dialog.setVisible(true);
    }

    @SuppressWarnings("unchecked")
    private void showDeleteConfirmDialog() {
        Frame frame = JOptionPane.getFrameForComponent(CrudTablePanel.this);
        String componentPluralCaption = Translator.getComponentPluralCaption(representation.getComponentClass());
        int result = JOptionPane.showConfirmDialog(frame,
                Translator.toLocale(TranslateCode.DELETION_CONFIRM_MESSAGE).formatted(componentPluralCaption.toLowerCase()),
                Translator.toLocale(TranslateCode.DELETE_DIALOG_TITLE).formatted(componentPluralCaption),
                JOptionPane.YES_NO_OPTION);
        if(result == JOptionPane.YES_OPTION) {
            List<C> selectedComponents = getSelectedComponents();
            List<I> ids = selectedComponents.stream()
                    .map(Persistent::getId)
                    .toList();
            DeleteTask<C, I, CrudService<C, I>> task = new DeleteTask<>(ServiceHolder.getCrudService(representation.getComponentClass()), ids);
            if(crudListenersMap.containsKey(CrudAction.DELETE)) {
                crudListenersMap.get(CrudAction.DELETE)
                        .forEach(finishListener -> task.addFinishListener((FinishListener<List<C>>) finishListener));
            }
            task.addFinishListener(components -> actionEnabling());
            exceptionListeners.forEach(task::addExceptionListener);
            ProgressDialog.initiate(task);
            TaskManager.run(task);
        }
    }

    public ComponentRepresentation<C> getRepresentation() {
        return representation;
    }

    protected CrudStyle getCrudStyle() {
        return crudStyle;
    }

    public Optional<C> getSelectedComponent() {
        return Optional.ofNullable(model.get(table.getSelectedRow()));
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

    public TableModel<C> getModel() {
        return model;
    }

    public JTable getTable() {
        return table;
    }
}
