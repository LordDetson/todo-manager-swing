package by.babanin.todo.view.component;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;

public class MenuAction extends AbstractAction {

    private final transient Runnable runnable;

    public MenuAction(String name, Icon icon, int mnemonicKey, Runnable runnable) {
        super(name, icon);
        this.runnable = runnable;
        putValue(Action.MNEMONIC_KEY, mnemonicKey);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        runnable.run();
    }
}
