package by.babanin.todo.view.component;

import static by.babanin.todo.view.translat.TranslateCode.*;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.swing.Icon;
import javax.swing.KeyStroke;
import javax.swing.UIManager;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.extras.FlatSVGIcon.ColorFilter;

import by.babanin.todo.image.IconResources;
import by.babanin.todo.view.component.action.Action;
import by.babanin.todo.view.component.validation.ValidatorFactory;
import by.babanin.todo.view.exception.ViewException;
import by.babanin.todo.view.translat.Translator;
import by.babanin.todo.view.util.GUIUtils;

public class CrudStyle implements Serializable {

    private static final int DEFAULT_ICON_SIZE = 48;
    private static final String SHOW_CREATION_DIALOG_ACTION_ID = "showCreationDialog";
    private static final String SHOW_EDIT_DIALOG_ACTION_ID = "showEditDialog";
    private static final String SHOW_DELETE_CONFIRM_DIALOG_ACTION_ID = "showDeleteConfirmDialog";
    private static final String MOVE_UP_ACTION_ID = "moveUp";
    private static final String MOVE_DOWN_ACTION_ID = "moveDown";

    private String createButtonIconName = "plus";
    private String editButtonIconName = "pen_write";
    private String deleteButtonIconName = "minus";
    private String moveUpButtonIconName = "chevron_up";
    private String moveDownButtonIconName = "chevron_down";
    private String createButtonToolTipCode = TOOLTIP_BUTTON_CREATE;
    private String editButtonToolTipCode = TOOLTIP_BUTTON_EDIT;
    private String deleteButtonToolTipCode = TOOLTIP_BUTTON_DELETE;
    private String moveUpButtonToolTipCode = TOOLTIP_BUTTON_MOVE_UP;
    private String moveDownButtonToolTipCode = TOOLTIP_BUTTON_MOVE_DOWN;
    private int iconSize = DEFAULT_ICON_SIZE;
    private transient ValidatorFactory validatorFactory;
    private final List<String> excludedFieldFromCreationForm = new ArrayList<>();
    private final List<String> excludedFieldFromEditForm = new ArrayList<>();
    private final List<String> excludedFieldFromTable = new ArrayList<>();

    private final Map<String, Action> crudActionMap = new HashMap<>();
    private final Map<String, Consumer<ActionEvent>> crudActionImplMap = new HashMap<>();
    private Supplier<Action> actionFactory = Action::new;

    private Icon getCreateButtonIcon(int iconSize) {
        return getIcon(createButtonIconName, iconSize);
    }

    public CrudStyle setCreateButtonIconName(String createButtonIconName) {
        this.createButtonIconName = createButtonIconName;
        return this;
    }

    private Icon getEditButtonIcon(int iconSize) {
        return getIcon(editButtonIconName, iconSize);
    }

    public CrudStyle setEditButtonIconName(String editButtonIconName) {
        this.editButtonIconName = editButtonIconName;
        return this;
    }

    private Icon getDeleteButtonIcon(int iconSize) {
        return getIcon(deleteButtonIconName, iconSize);
    }

    public CrudStyle setDeleteButtonIconName(String deleteButtonIconName) {
        this.deleteButtonIconName = deleteButtonIconName;
        return this;
    }

    private Icon getMoveUpButtonIcon(int iconSize) {
        return getIcon(moveUpButtonIconName, iconSize);
    }

    public CrudStyle setMoveUpButtonIconName(String moveUpButtonIconName) {
        this.moveUpButtonIconName = moveUpButtonIconName;
        return this;
    }

    private Icon getMoveDownButtonIcon(int iconSize) {
        return getIcon(moveDownButtonIconName, iconSize);
    }

    public Icon getIcon(String name, int iconSize) {
        FlatSVGIcon icon = IconResources.getIcon(name, iconSize);
        ColorFilter colorFilter = new ColorFilter();
        colorFilter.add(Color.BLACK, UIManager.getDefaults().getColor("Button.foreground"));
        icon.setColorFilter(colorFilter);
        return icon;
    }

    public String getCreateButtonToolTip() {
        return Translator.toLocale(createButtonToolTipCode);
    }

    public CrudStyle setCreateButtonToolTipCode(String createButtonToolTipCode) {
        this.createButtonToolTipCode = createButtonToolTipCode;
        return this;
    }

    public String getEditButtonToolTip() {
        return Translator.toLocale(editButtonToolTipCode);
    }

    public CrudStyle setEditButtonToolTipCode(String editButtonToolTipCode) {
        this.editButtonToolTipCode = editButtonToolTipCode;
        return this;
    }

    public String getDeleteButtonToolTip() {
        return Translator.toLocale(deleteButtonToolTipCode);
    }

    public CrudStyle setDeleteButtonToolTipCode(String deleteButtonToolTipCode) {
        this.deleteButtonToolTipCode = deleteButtonToolTipCode;
        return this;
    }

    public String getMoveUpButtonToolTip() {
        return Translator.toLocale(moveUpButtonToolTipCode);
    }

    public CrudStyle setMoveUpButtonToolTipCode(String moveUpButtonToolTipCode) {
        this.moveUpButtonToolTipCode = moveUpButtonToolTipCode;
        return this;
    }

    public String getMoveDownButtonToolTip() {
        return Translator.toLocale(moveDownButtonToolTipCode);
    }

    public CrudStyle setMoveDownButtonToolTipCode(String moveDownButtonToolTipCode) {
        this.moveDownButtonToolTipCode = moveDownButtonToolTipCode;
        return this;
    }

    public CrudStyle setMoveDownButtonIconName(String moveDownButtonIconName) {
        this.moveDownButtonIconName = moveDownButtonIconName;
        return this;
    }

    public int getIconSize() {
        return iconSize;
    }

    public CrudStyle setIconSize(int iconSize) {
        this.iconSize = iconSize;
        return this;
    }

    public Optional<ValidatorFactory> getValidatorFactory() {
        return Optional.ofNullable(validatorFactory);
    }

    public CrudStyle setValidatorFactory(ValidatorFactory validatorFactory) {
        this.validatorFactory = validatorFactory;
        return this;
    }

    public List<String> getExcludedFieldFromCreationForm() {
        return Collections.unmodifiableList(excludedFieldFromCreationForm);
    }

    public CrudStyle excludeFieldFromCreationForm(String... fields) {
        this.excludedFieldFromCreationForm.addAll(List.of(fields));
        return this;
    }

    public List<String> getExcludedFieldFromEditForm() {
        return Collections.unmodifiableList(excludedFieldFromEditForm);
    }

    public CrudStyle excludeFieldFromEditForm(String... fields) {
        this.excludedFieldFromEditForm.addAll(List.of(fields));
        return this;
    }

    public List<String> getExcludedFieldFromTable() {
        return Collections.unmodifiableList(excludedFieldFromTable);
    }

    public CrudStyle excludeFieldFromTable(String... fields) {
        this.excludedFieldFromTable.addAll(List.of(fields));
        return this;
    }

    @SuppressWarnings("unchecked")
    public void setActionFactory(Supplier<? extends Action> actionFactory) {
        this.actionFactory = (Supplier<Action>) actionFactory;
    }

    public void setShowCreationDialogActionImpl(Consumer<ActionEvent> actionImpl) {
        crudActionImplMap.put(SHOW_CREATION_DIALOG_ACTION_ID, actionImpl);
    }

    public Action getShowCreationDialogAction() {
        return crudActionMap.computeIfAbsent(SHOW_CREATION_DIALOG_ACTION_ID,
                id -> Action.builder(actionFactory)
                        .id(id)
                        .name(getCreateButtonToolTip())
                        .toolTip(getCreateButtonToolTip())
                        .smallIcon(getCreateButtonIcon(GUIUtils.DEFAULT_MENU_ICON_SIZE))
                        .largeIcon(getCreateButtonIcon(iconSize))
                        .accelerator(KeyStroke.getKeyStroke("control SPACE"))
                        .action(getActionImpl(id))
                        .build());
    }

    public void setShowEditDialogActionImpl(Consumer<ActionEvent> actionImpl) {
        crudActionImplMap.put(SHOW_EDIT_DIALOG_ACTION_ID, actionImpl);
    }

    public Action getShowEditDialogAction() {
        return crudActionMap.computeIfAbsent(SHOW_EDIT_DIALOG_ACTION_ID,
                id -> Action.builder(actionFactory)
                        .id(id)
                        .name(getEditButtonToolTip())
                        .toolTip(getEditButtonToolTip())
                        .smallIcon(getEditButtonIcon(GUIUtils.DEFAULT_MENU_ICON_SIZE))
                        .largeIcon(getEditButtonIcon(iconSize))
                        .accelerator(KeyStroke.getKeyStroke("control E"))
                        .action(getActionImpl(id))
                        .build());
    }

    public void setShowDeleteConfirmDialogActionImpl(Consumer<ActionEvent> actionImpl) {
        crudActionImplMap.put(SHOW_DELETE_CONFIRM_DIALOG_ACTION_ID, actionImpl);
    }

    public Action getShowDeleteConfirmDialogAction() {
        return crudActionMap.computeIfAbsent(SHOW_DELETE_CONFIRM_DIALOG_ACTION_ID,
                id -> Action.builder(actionFactory)
                        .id(id)
                        .name(getDeleteButtonToolTip())
                        .toolTip(getDeleteButtonToolTip())
                        .smallIcon(getDeleteButtonIcon(GUIUtils.DEFAULT_MENU_ICON_SIZE))
                        .largeIcon(getDeleteButtonIcon(iconSize))
                        .accelerator(KeyStroke.getKeyStroke("DELETE"))
                        .action(getActionImpl(id))
                        .build());
    }

    public void setMoveUpActionImpl(Consumer<ActionEvent> actionImpl) {
        crudActionImplMap.put(MOVE_UP_ACTION_ID, actionImpl);
    }

    public Action getMoveUpAction() {
        return crudActionMap.computeIfAbsent(MOVE_UP_ACTION_ID,
                id -> Action.builder(actionFactory)
                        .id(id)
                        .name(getMoveUpButtonToolTip())
                        .toolTip(getMoveUpButtonToolTip())
                        .smallIcon(getMoveUpButtonIcon(GUIUtils.DEFAULT_MENU_ICON_SIZE))
                        .largeIcon(getMoveUpButtonIcon(iconSize))
                        .accelerator(KeyStroke.getKeyStroke("control UP"))
                        .action(getActionImpl(id))
                        .build());
    }

    public void setMoveDownActionImpl(Consumer<ActionEvent> actionImpl) {
        crudActionImplMap.put(MOVE_DOWN_ACTION_ID, actionImpl);
    }

    public Action getMoveDownAction() {
        return crudActionMap.computeIfAbsent(MOVE_DOWN_ACTION_ID,
                id -> Action.builder(actionFactory)
                        .id(id)
                        .name(getMoveDownButtonToolTip())
                        .toolTip(getMoveDownButtonToolTip())
                        .smallIcon(getMoveDownButtonIcon(GUIUtils.DEFAULT_MENU_ICON_SIZE))
                        .largeIcon(getMoveDownButtonIcon(iconSize))
                        .accelerator(KeyStroke.getKeyStroke("control DOWN"))
                        .action(getActionImpl(id))
                        .build());
    }

    private Consumer<ActionEvent> getActionImpl(String id) {
        Consumer<ActionEvent> action = crudActionImplMap.get(id);
        if(action == null) {
            throw new ViewException("No action implementation found for " + id);
        }
        return action;
    }
}
