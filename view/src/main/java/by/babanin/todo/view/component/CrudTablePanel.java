package by.babanin.todo.view.component;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.swing.Action;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import by.babanin.todo.application.service.CrudService;
import by.babanin.todo.model.Persistent;
import by.babanin.todo.representation.ComponentRepresentation;
import by.babanin.todo.representation.ReportField;
import by.babanin.todo.task.DeleteTask;
import by.babanin.todo.task.GetTask;
import by.babanin.todo.task.Task;
import by.babanin.todo.task.listener.ExceptionListener;
import by.babanin.todo.task.listener.FinishListener;
import by.babanin.todo.view.component.form.ComponentForm;
import by.babanin.todo.view.component.form.FormRowFactory;
import by.babanin.todo.view.exception.ViewException;
import by.babanin.todo.view.translat.TranslateCode;
import by.babanin.todo.view.translat.Translator;
import by.babanin.todo.view.util.ServiceHolder;

public abstract class CrudTablePanel<C extends Persistent<I>, I> extends JPanel implements View {

    private final Map<CrudAction, List<FinishListener<?>>> crudListenersMap = new EnumMap<>(CrudAction.class);
    private final List<ExceptionListener> exceptionListeners = new ArrayList<>();
    private final transient ComponentRepresentation<C> representation;
    private final transient FormRowFactory formRowFactory;
    private final CrudStyle crudStyle;

    private transient Action showCreationDialogAction;
    private transient Action showEditDialogAction;
    private transient Action showDeleteConfirmDialogAction;
    private TableModel<C> model;
    private CustomTableColumnModel columnModel;
    private JTable table;

    protected CrudTablePanel(Class<C> componentClass, FormRowFactory formRowFactory, CrudStyle crudStyle) {
        this.representation = ComponentRepresentation.get(componentClass);
        this.formRowFactory = formRowFactory;
        this.crudStyle = crudStyle;
        initialize();
    }

    @Override
    public void initialize() {
        createUiComponents();
        setupTable(table, model, columnModel);
        addListeners();
        placeComponents();
        actionEnabling();
    }

    @Override
    public void createUiComponents() {
        List<String> excludedFieldFromTable = crudStyle.getExcludedFieldFromTable();
        List<ReportField> reportFields = representation.getFields().stream()
                .filter(field -> !excludedFieldFromTable.contains(field.getName()))
                .toList();
        model = createTableModel(representation, reportFields);
        columnModel = new CustomTableColumnModel(reportFields);
        table = new JTable();

        showCreationDialogAction = new RunnableAction(
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
    }

    protected TableModel<C> createTableModel(ComponentRepresentation<C> representation, List<ReportField> fields) {
        return new TableModel<>(representation, fields);
    }

    protected void setupTable(JTable table, TableModel<C> model, CustomTableColumnModel columnModel) {
        table.setModel(model);
        table.setColumnModel(columnModel);
    }

    @Override
    public void addListeners() {
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

    @Override
    public void placeComponents() {
        setLayout(new BorderLayout());
        add(new JScrollPane(table), BorderLayout.CENTER);
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

    @Override
    @SuppressWarnings("unchecked")
    public void load() {
        GetTask<C, I, CrudService<C, I>> task = new GetTask<>(ServiceHolder.getCrudService(representation.getComponentClass()));
        if(crudListenersMap.containsKey(CrudAction.READ)) {
            crudListenersMap.get(CrudAction.READ)
                    .forEach(finishListener -> task.addFinishListener((FinishListener<List<C>>) finishListener));
        }
        task.addFinishListener(components -> actionEnabling());
        exceptionListeners.forEach(task::addExceptionListener);
        task.execute();
    }

    @Override
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
        task.execute();
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
        task.execute();
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

    public Action getShowCreationDialogAction() {
        return showCreationDialogAction;
    }

    public Action getShowEditDialogAction() {
        return showEditDialogAction;
    }

    public Action getShowDeleteConfirmDialogAction() {
        return showDeleteConfirmDialogAction;
    }
}
