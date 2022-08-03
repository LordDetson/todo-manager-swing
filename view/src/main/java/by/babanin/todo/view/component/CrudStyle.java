package by.babanin.todo.view.component;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.swing.Icon;

import by.babanin.todo.view.component.validation.ValidatorFactory;
import by.babanin.todo.view.util.IconResources;

public class CrudStyle implements Serializable {

    private String createButtonIconName = "plus";
    private String editButtonIconName = "pen_write";
    private String deleteButtonIconName = "minus";
    private String moveUpButtonIconName = "chevron_up";
    private String moveDownButtonIconName = "chevron_down";
    private transient ValidatorFactory validatorFactory;
    private final List<String> excludedFieldFromCreationForm = new ArrayList<>();
    private final List<String> excludedFieldFromEditForm = new ArrayList<>();

    public Icon getCreateButtonIcon() {
        return IconResources.getIcon(createButtonIconName);
    }

    public CrudStyle setCreateButtonIconName(String createButtonIconName) {
        this.createButtonIconName = createButtonIconName;
        return this;
    }

    public Icon getEditButtonIcon() {
        return IconResources.getIcon(editButtonIconName);
    }

    public CrudStyle setEditButtonIconName(String editButtonIconName) {
        this.editButtonIconName = editButtonIconName;
        return this;
    }

    public Icon getDeleteButtonIcon() {
        return IconResources.getIcon(deleteButtonIconName);
    }

    public CrudStyle setDeleteButtonIconName(String deleteButtonIconName) {
        this.deleteButtonIconName = deleteButtonIconName;
        return this;
    }

    public Icon getMoveUpButtonIcon() {
        return IconResources.getIcon(moveUpButtonIconName);
    }

    public CrudStyle setMoveUpButtonIconName(String moveUpButtonIconName) {
        this.moveUpButtonIconName = moveUpButtonIconName;
        return this;
    }

    public Icon getMoveDownButtonIcon() {
        return IconResources.getIcon(moveDownButtonIconName);
    }

    public CrudStyle setMoveDownButtonIconName(String moveDownButtonIconName) {
        this.moveDownButtonIconName = moveDownButtonIconName;
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
}
