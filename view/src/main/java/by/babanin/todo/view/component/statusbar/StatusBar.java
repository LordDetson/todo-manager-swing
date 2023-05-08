package by.babanin.todo.view.component.statusbar;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JPanel;

import by.babanin.ext.component.util.GUIUtils;
import by.babanin.todo.view.exception.ViewException;

public class StatusBar extends JPanel {

    private static final Dimension DEFAULT_SIZE = new Dimension(150, 26);

    public StatusBar() {
        GUIUtils.removeFocus(this);
        updateUI();
        setLayout(new FlowLayout(FlowLayout.LEFT));
        setMinimumSize(DEFAULT_SIZE);
        setPreferredSize(DEFAULT_SIZE);
    }

    @Override
    protected void addImpl(Component component, Object constraints, int index) {
        if(component instanceof StatusBarItem statusBarItem) {
            super.addImpl(statusBarItem, constraints, index);
        }
        else {
            throw new ViewException(component.getClass() + " component doesn't support");
        }
    }
}