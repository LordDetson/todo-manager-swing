package by.babanin.todo.view.component;

import static by.babanin.todo.view.translat.TranslateCode.*;

import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.swing.Icon;
import javax.swing.UIManager;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.extras.FlatSVGIcon.ColorFilter;

import by.babanin.todo.view.component.validation.ValidatorFactory;
import by.babanin.todo.view.translat.Translator;
import by.babanin.todo.view.util.IconResources;

public class CrudStyle implements Serializable {

    private static final int DEFAULT_ICON_SIZE = 48;

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

    public Icon getCreateButtonIcon() {
        return getIcon(createButtonIconName);
    }

    public CrudStyle setCreateButtonIconName(String createButtonIconName) {
        this.createButtonIconName = createButtonIconName;
        return this;
    }

    public Icon getEditButtonIcon() {
        return getIcon(editButtonIconName);
    }

    public CrudStyle setEditButtonIconName(String editButtonIconName) {
        this.editButtonIconName = editButtonIconName;
        return this;
    }

    public Icon getDeleteButtonIcon() {
        return getIcon(deleteButtonIconName);
    }

    public CrudStyle setDeleteButtonIconName(String deleteButtonIconName) {
        this.deleteButtonIconName = deleteButtonIconName;
        return this;
    }

    public Icon getMoveUpButtonIcon() {
        return getIcon(moveUpButtonIconName);
    }

    public CrudStyle setMoveUpButtonIconName(String moveUpButtonIconName) {
        this.moveUpButtonIconName = moveUpButtonIconName;
        return this;
    }

    public Icon getMoveDownButtonIcon() {
        return getIcon(moveDownButtonIconName);
    }

    public Icon getIcon(String name) {
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
}
