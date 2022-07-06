package by.babanin.todo.view.component.crud;

import javax.swing.Icon;

import by.babanin.todo.view.util.IconResources;

public class CrudStyle {

    private String createButtonIconName = "plus";
    private String editButtonIconName = "pen_write";
    private String deleteButtonIconName = "minus";
    private String moveUpButtonIconName = "chevron_up";
    private String moveDownButtonIconName = "chevron_down";

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
}
