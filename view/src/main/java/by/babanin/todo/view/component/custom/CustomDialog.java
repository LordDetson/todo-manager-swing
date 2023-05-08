package by.babanin.todo.view.component.custom;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.Window;

import javax.swing.JDialog;

import by.babanin.ext.component.util.GUIUtils;

public class CustomDialog extends JDialog {

    private static final String DIALOG_CLOSING_ACTION_KEY = "closeDialog";

    public CustomDialog() {
    }

    public CustomDialog(Frame owner) {
        super(owner);
    }

    public CustomDialog(Frame owner, boolean modal) {
        super(owner, modal);
    }

    public CustomDialog(Frame owner, String title) {
        super(owner, title);
    }

    public CustomDialog(Frame owner, String title, boolean modal) {
        super(owner, title, modal);
    }

    public CustomDialog(Frame owner, String title, boolean modal, GraphicsConfiguration gc) {
        super(owner, title, modal, gc);
    }

    public CustomDialog(Dialog owner) {
        super(owner);
    }

    public CustomDialog(Dialog owner, boolean modal) {
        super(owner, modal);
    }

    public CustomDialog(Dialog owner, String title) {
        super(owner, title);
    }

    public CustomDialog(Dialog owner, String title, boolean modal) {
        super(owner, title, modal);
    }

    public CustomDialog(Dialog owner, String title, boolean modal, GraphicsConfiguration gc) {
        super(owner, title, modal, gc);
    }

    public CustomDialog(Window owner) {
        super(owner);
    }

    public CustomDialog(Window owner, ModalityType modalityType) {
        super(owner, modalityType);
    }

    public CustomDialog(Window owner, String title) {
        super(owner, title);
    }

    public CustomDialog(Window owner, String title, ModalityType modalityType) {
        super(owner, title, modalityType);
    }

    public CustomDialog(Window owner, String title, ModalityType modalityType, GraphicsConfiguration gc) {
        super(owner, title, modalityType, gc);
    }

    @Override
    protected void dialogInit() {
        super.dialogInit();
        GUIUtils.addCloseActionOnEscape(this, DIALOG_CLOSING_ACTION_KEY);
    }
}
