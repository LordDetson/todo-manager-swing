package by.babanin.todo.view.component;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;

import by.babanin.todo.view.util.GUIUtils;

public class ToolAction extends AbstractAction {

    private final transient Runnable runnable;

    public ToolAction(Icon icon, String toolTip, int mnemonicKey, Runnable runnable) {
        this.runnable = runnable;
        putValue(Action.SMALL_ICON, icon);
        putValue(Action.MNEMONIC_KEY, mnemonicKey);
        putValue(Action.SHORT_DESCRIPTION, toolTip + " " + GUIUtils.getMnemonicKeyDescription(mnemonicKey));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        runnable.run();
    }
}
