package by.babanin.todo.view.component.crud;

import java.awt.Component;
import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.swing.Action;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

import by.babanin.todo.application.service.CrudService;
import by.babanin.todo.model.Persistent;
import by.babanin.todo.representation.ComponentRepresentation;
import by.babanin.todo.representation.ReportField;
import by.babanin.todo.task.DeleteTask;
import by.babanin.todo.task.GetTask;
import by.babanin.todo.task.Task;
import by.babanin.todo.task.listener.ExceptionListener;
import by.babanin.todo.task.listener.FinishListener;
import by.babanin.todo.view.component.CrudStyle;
import by.babanin.todo.view.component.RunnableAction;
import by.babanin.todo.view.component.form.ComponentForm;
import by.babanin.todo.view.component.form.FormRowFactory;
import by.babanin.todo.view.exception.ViewException;
import by.babanin.todo.view.translat.TranslateCode;
import by.babanin.todo.view.translat.Translator;

public abstract class Crud<C extends Persistent<I>, I, S extends CrudService<C, I>> {

    private final Map<CrudAction, List<FinishListener<?>>> crudListenersMap = new EnumMap<>(CrudAction.class);
    private final List<ExceptionListener> exceptionListeners = new ArrayList<>();

    private final Component owner;
    private final S crudService;
    private final ComponentRepresentation<C> representation;
    private final FormRowFactory formRowFactory;
    private final CrudStyle crudStyle;
    private final Action showCreationDialogAction;
    private final Action showUpdateDialogAction;
    private final Action showDeletionConfirmationDialogAction;

    private Supplier<Boolean> canCreateFunction;
    private Function<C, Boolean> canUpdateFunction;
    private Supplier<C> componentToUpdateSupplier;
    private Function<List<C>, Boolean> canDeleteFunction;
    private Supplier<List<C>> componentsToDeleteSupplier;

    protected Crud(Component owner, S crudService, ComponentRepresentation<C> representation, FormRowFactory formRowFactory, CrudStyle crudStyle) {
        this.owner = owner;
        this.crudService = crudService;
        this.representation = representation;
        this.formRowFactory = formRowFactory;
        this.crudStyle = crudStyle;

        showCreationDialogAction = new RunnableAction(
                crudStyle.getCreateButtonIcon(),
                crudStyle.getCreateButtonToolTip(),
                KeyEvent.VK_C,
                this::showCreationDialog
        );
        showUpdateDialogAction = new RunnableAction(
                crudStyle.getEditButtonIcon(),
                crudStyle.getEditButtonToolTip(),
                KeyEvent.VK_E,
                () -> showUpdateDialog(getComponentToUpdate())
        );
        showDeletionConfirmationDialogAction = new RunnableAction(
                crudStyle.getDeleteButtonIcon(),
                crudStyle.getDeleteButtonToolTip(),
                KeyEvent.VK_D,
                () -> showDeletionConfirmationDialog(getComponentsToDelete())
        );
    }

    @SuppressWarnings("unchecked")
    public void create(Map<ReportField, ?> fieldValueMap) {
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

    @SuppressWarnings("unchecked")
    public void read() {
        Task<List<C>> task = new GetTask<>(crudService);
        if(crudListenersMap.containsKey(CrudAction.READ)) {
            crudListenersMap.get(CrudAction.READ)
                    .forEach(finishListener -> task.addFinishListener((FinishListener<List<C>>) finishListener));
        }
        task.addFinishListener(components -> actionEnabling());
        exceptionListeners.forEach(task::addExceptionListener);
        task.execute();
    }

    @SuppressWarnings("unchecked")
    public void update(Map<ReportField, ?> fieldValueMap, C component) {
        Task<C> task = createUpdateTask(fieldValueMap, component);
        if(crudListenersMap.containsKey(CrudAction.UPDATE)) {
            crudListenersMap.get(CrudAction.UPDATE)
                    .forEach(finishListener -> task.addFinishListener((FinishListener<C>) finishListener));
        }
        task.addFinishListener(result -> actionEnabling());
        exceptionListeners.forEach(task::addExceptionListener);
        task.execute();
    }

    protected abstract Task<C> createUpdateTask(Map<ReportField, ?> fieldValueMap, C component);

    @SuppressWarnings("unchecked")
    public void delete(List<C> components) {
        List<I> ids = components.stream()
                .filter(Objects::nonNull)
                .map(Persistent::getId)
                .toList();
        Task<List<C>> task = new DeleteTask<>(crudService, ids);
        if(crudListenersMap.containsKey(CrudAction.DELETE)) {
            crudListenersMap.get(CrudAction.DELETE)
                    .forEach(finishListener -> task.addFinishListener((FinishListener<List<C>>) finishListener));
        }
        task.addFinishListener(result -> actionEnabling());
        exceptionListeners.forEach(task::addExceptionListener);
        task.execute();
    }

    public boolean canCreate() {
        return canCreateFunction == null || canCreateFunction.get();
    }

    public boolean canUpdate(C component) {
        return component != null && (canUpdateFunction == null || canUpdateFunction.apply(component));
    }

    public boolean canDelete(List<C> components) {
        components = components.stream()
                .filter(Objects::nonNull)
                .toList();
        return !components.isEmpty() && (canDeleteFunction == null || canDeleteFunction.apply(components));
    }

    public void actionEnabling() {
        getShowCreationDialogAction().setEnabled(canCreate());
        getShowUpdateDialogAction().setEnabled(canUpdate(getComponentToUpdate()));
        getShowDeletionConfirmationDialogAction().setEnabled(canDelete(getComponentsToDelete()));
    }

    public void addCreationListener(FinishListener<C> listener) {
        addCrudListener(CrudAction.CREATE, listener);
    }

    public void addReadListener(FinishListener<List<C>> listener) {
        addCrudListener(CrudAction.READ, listener);
    }

    public void addUpdateListener(FinishListener<C> listener) {
        addCrudListener(CrudAction.UPDATE, listener);
    }

    public void addDeletionListener(FinishListener<List<C>> listener) {
        addCrudListener(CrudAction.DELETE, listener);
    }

    private void addCrudListener(CrudAction crudAction, FinishListener<?> listener) {
        crudListenersMap.computeIfAbsent(crudAction, action -> new ArrayList<>()).add(listener);
    }

    public void addExceptionListener(ExceptionListener listener) {
        exceptionListeners.add(listener);
    }

    public Action getShowCreationDialogAction() {
        return showCreationDialogAction;
    }

    public Action getShowUpdateDialogAction() {
        return showUpdateDialogAction;
    }

    public Action getShowDeletionConfirmationDialogAction() {
        return showDeletionConfirmationDialogAction;
    }

    public void showCreationDialog() {
        ComponentForm<C> form = new ComponentForm<>(representation.getComponentClass(), formRowFactory, crudStyle);
        form.addApplyListener(this::create);
        showComponentForm(form, TranslateCode.CREATION_DIALOG_TITLE);
    }

    public void showUpdateDialog(C component) {
        if(component == null) {
            throw new ViewException("Can't open edit dialog because component is empty");
        }
        ComponentForm<C> form = new ComponentForm<>(representation.getComponentClass(), formRowFactory, crudStyle, component);
        form.addApplyListener(fieldValueMap -> update(fieldValueMap, component));
        showComponentForm(form, TranslateCode.EDIT_DIALOG_TITLE);
    }

    public void showDeletionConfirmationDialog(List<C> components) {
        String componentPluralCaption = Translator.getComponentPluralCaption(representation.getComponentClass());
        int result = JOptionPane.showConfirmDialog(owner,
                Translator.toLocale(TranslateCode.DELETION_CONFIRM_MESSAGE).formatted(componentPluralCaption.toLowerCase()),
                Translator.toLocale(TranslateCode.DELETE_DIALOG_TITLE).formatted(componentPluralCaption),
                JOptionPane.YES_NO_OPTION);
        if(result == JOptionPane.YES_OPTION) {
            delete(components);
        }
    }

    public S getCrudService() {
        return crudService;
    }

    public ComponentRepresentation<C> getRepresentation() {
        return representation;
    }

    public CrudStyle getCrudStyle() {
        return crudStyle;
    }

    public void setCanCreateFunction(Supplier<Boolean> canCreateFunction) {
        this.canCreateFunction = canCreateFunction;
    }

    public void setCanUpdateFunction(Function<C, Boolean> canUpdateFunction) {
        this.canUpdateFunction = canUpdateFunction;
    }

    public void setComponentToUpdateSupplier(Supplier<C> componentToUpdateSupplier) {
        this.componentToUpdateSupplier = componentToUpdateSupplier;
    }

    public C getComponentToUpdate() {
        return componentToUpdateSupplier.get();
    }

    public void setCanDeleteFunction(Function<List<C>, Boolean> canDeleteFunction) {
        this.canDeleteFunction = canDeleteFunction;
    }

    public void setComponentsToDeleteSupplier(Supplier<List<C>> componentsToDeleteSupplier) {
        this.componentsToDeleteSupplier = componentsToDeleteSupplier;
    }

    public List<C> getComponentsToDelete() {
        return componentsToDeleteSupplier.get();
    }

    private void showComponentForm(ComponentForm<C> form, String titleCode) {
        Frame frame = JOptionPane.getFrameForComponent(owner);
        JDialog dialog = new JDialog(frame, true);
        dialog.setContentPane(form);
        form.setOwner(dialog);
        dialog.setTitle(Translator.toLocale(titleCode).formatted(Translator.getComponentCaption(representation.getComponentClass())));
        dialog.pack();
        dialog.setLocationRelativeTo(frame);
        dialog.setMinimumSize(dialog.getSize());
        dialog.setVisible(true);
    }
}
