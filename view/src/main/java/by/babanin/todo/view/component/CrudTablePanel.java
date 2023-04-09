package by.babanin.todo.view.component;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;

import by.babanin.todo.application.service.CrudService;
import by.babanin.todo.model.Persistent;
import by.babanin.todo.preferences.PreferenceAware;
import by.babanin.todo.preferences.PreferencesGroup;
import by.babanin.todo.representation.ComponentRepresentation;
import by.babanin.todo.representation.ReportField;
import by.babanin.todo.task.DeleteTask;
import by.babanin.todo.task.GetTask;
import by.babanin.todo.task.Task;
import by.babanin.todo.task.listener.ExceptionListener;
import by.babanin.todo.task.listener.FinishListener;
import by.babanin.todo.view.component.form.ComponentForm;
import by.babanin.todo.view.component.form.FormDialog;
import by.babanin.todo.view.component.form.FormRowFactory;
import by.babanin.todo.view.exception.ViewException;
import by.babanin.todo.view.preference.TableColumnsPreference;
import by.babanin.todo.view.translat.TranslateCode;
import by.babanin.todo.view.translat.Translator;
import by.babanin.todo.view.util.GUIUtils;

public abstract class CrudTablePanel<C extends Persistent<I>, I> extends JPanel
        implements PreferenceAware<PreferencesGroup> {

    private static final String TABLE_COLUMNS_PREFERENCE_KEY = "tableColumnsPreference";

    private final Map<CrudAction, List<FinishListener<?>>> crudListenersMap = new EnumMap<>(CrudAction.class);
    private final List<ExceptionListener> exceptionListeners = new ArrayList<>();

    private final transient CrudService<C, I> service;
    private final transient ComponentRepresentation<C> representation;
    private final transient FormRowFactory formRowFactory;
    private final CrudStyle crudStyle;

    private JToolBar toolBar;

    private transient Action showEditDialogAction;
    private transient Action showDeleteConfirmDialogAction;
    private JButton createButton;
    private JButton editButton;
    private JButton deleteButton;
    private TableModel<C> model;
    private CustomTableColumnModel columnModel;
    private CustomTableColumnModel defaultColumnModel;
    private JTable table;

    protected CrudTablePanel(CrudService<C, I> service, Class<C> componentClass, FormRowFactory formRowFactory, CrudStyle crudStyle) {
        this.service = service;
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
        defaultColumnModel = createColumnModel(reportFields);
        columnModel = createColumnModel(reportFields);
        table = new JTable();

        Action showCreationDialogAction = new ToolAction(
                crudStyle.getCreateButtonIcon(),
                crudStyle.getCreateButtonToolTip(),
                KeyEvent.VK_C,
                this::showCreationDialog
        );
        showEditDialogAction = new ToolAction(
                crudStyle.getEditButtonIcon(),
                crudStyle.getEditButtonToolTip(),
                KeyEvent.VK_E,
                this::showEditDialog
        );
        showDeleteConfirmDialogAction = new ToolAction(
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

    protected CustomTableColumnModel createColumnModel(List<ReportField> reportFields) {
        return new CustomTableColumnModel(reportFields);
    }

    protected void setupTable(JTable table, TableModel<C> model, CustomTableColumnModel columnModel) {
        table.setModel(model);
        table.setColumnModel(columnModel);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    }

    protected void addListeners() {
        table.getSelectionModel().addListSelectionListener(event -> actionEnabling());

        addCreationListener(this::handleCreation);
        addLoadListener(this::handleLoad);
        addEditListener(this::handleEdit);
        addDeletionListener(this::handleDeletion);
        addExceptionListener(this::handleException);
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

    protected void handleCreation(C result) {
        model.add(result);
        selectComponent(result);
    }

    protected void handleLoad(List<C> result) {
        model.addAll(result);
        selectFirstRow();
    }

    protected void handleEdit(C result) {
        int row = table.getSelectedRow();
        model.set(row, result);
        selectRow(row);
    }

    protected void handleDeletion(List<C> result) {
        model.remove(result);
    }

    protected void handleException(Exception exception) {
        reload();
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
        GetTask<C, I, CrudService<C, I>> task = new GetTask<>(service);
        if(crudListenersMap.containsKey(CrudAction.READ)) {
            crudListenersMap.get(CrudAction.READ)
                    .forEach(finishListener -> task.addFinishListener((FinishListener<List<C>>) finishListener));
        }
        task.addFinishListener(components -> actionEnabling());
        exceptionListeners.forEach(task::addExceptionListener);
        task.execute();
    }

    public void clear() {
        table.clearSelection();
        model.clear();
    }

    public void reload() {
        clear();
        load();
    }

    private void showCreationDialog() {
        ComponentForm<C> form = new ComponentForm<>(representation.getComponentClass(), formRowFactory, crudStyle);
        form.addApplyListener(this::runCreationTask);
        FormDialog<C> formDialog = new FormDialog<>(this, form, TranslateCode.CREATION_DIALOG_TITLE);
        formDialog.setName(form.getComponentRepresentation().getComponentClass().getSimpleName() + "CreationFormDialog");
        GUIUtils.addPreferenceSupport(formDialog);
        formDialog.setVisible(true);
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
        task.execute();
    }

    protected abstract Task<C> createCreationTask(Map<ReportField, ?> fieldValueMap);

    private void showEditDialog() {
        C selectedComponent = getSelectedComponent()
                .orElseThrow(() -> new ViewException("Can't open edit dialog because component is not selected"));
        ComponentForm<C> form = new ComponentForm<>(representation.getComponentClass(), formRowFactory, crudStyle, selectedComponent);
        form.addApplyListener(fieldValueMap -> runUpdateTask(fieldValueMap, selectedComponent));
        FormDialog<C> formDialog = new FormDialog<>(this, form, TranslateCode.EDIT_DIALOG_TITLE);
        formDialog.setName(form.getComponentRepresentation().getComponentClass().getSimpleName() + "EditFormDialog");
        GUIUtils.addPreferenceSupport(formDialog);
        formDialog.setVisible(true);
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
        task.execute();
    }

    protected abstract Task<C> createUpdateTask(Map<ReportField, ?> fieldValueMap, C selectedComponent);

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
            DeleteTask<C, I, CrudService<C, I>> task = new DeleteTask<>(service, ids);
            if(crudListenersMap.containsKey(CrudAction.DELETE)) {
                crudListenersMap.get(CrudAction.DELETE)
                        .forEach(finishListener -> task.addFinishListener((FinishListener<List<C>>) finishListener));
            }
            task.addFinishListener(components -> actionEnabling());
            exceptionListeners.forEach(task::addExceptionListener);
            task.execute();
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
        table.scrollRectToVisible(new Rectangle(table.getCellRect(row, 0, true)));
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

    protected CrudService<C, I> getService() {
        return service;
    }

    @Override
    public void apply(PreferencesGroup preferencesGroup) {
        preferencesGroup.get(TABLE_COLUMNS_PREFERENCE_KEY)
                .ifPresent(preference -> ((TableColumnsPreference) preference).apply(columnModel));
    }

    @Override
    public PreferencesGroup createCurrentPreference() {
        TableColumnsPreference tableColumnsPreference = new TableColumnsPreference();
        tableColumnsPreference.add(columnModel);
        PreferencesGroup preferencesGroup = new PreferencesGroup();
        preferencesGroup.put(TABLE_COLUMNS_PREFERENCE_KEY, tableColumnsPreference);
        return preferencesGroup;
    }

    @Override
    public PreferencesGroup createDefaultPreference() {
        TableColumnsPreference tableColumnsPreference = new TableColumnsPreference();
        tableColumnsPreference.add(defaultColumnModel);
        PreferencesGroup preferencesGroup = new PreferencesGroup();
        preferencesGroup.put(TABLE_COLUMNS_PREFERENCE_KEY, tableColumnsPreference);
        return preferencesGroup;
    }

    @Override
    public String getKey() {
        return getName();
    }
}
