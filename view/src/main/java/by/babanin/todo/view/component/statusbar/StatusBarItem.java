package by.babanin.todo.view.component.statusbar;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import by.babanin.todo.view.util.GUIUtils;

public class StatusBarItem extends JPanel {

    private final JLabel label = new JLabel();

    public StatusBarItem() {
        GUIUtils.removeFocus(this);
        setLayout(new BorderLayout());
        add(label);
    }

    public JLabel getLabel() {
        return this.label;
    }

    public void setText(String text) {
        label.setText(text);
    }

    public String getText() {
        return label.getText();
    }

    @Override
    public void setToolTipText(String toolTipText) {
        label.setToolTipText(toolTipText);
    }

    @Override
    public String getToolTipText() {
        return label.getToolTipText();
    }

    public void setIcon(Icon icon) {
        label.setIcon(icon);
    }

    public Icon getIcon() {
        return label.getIcon();
    }

    @Override
    public void setEnabled(boolean enabled) {
        label.setEnabled(enabled);
    }

    @Override
    public Dimension getPreferredSize() {
        return label.getPreferredSize();
    }

    @Override
    public synchronized void addMouseListener(MouseListener listener) {
        label.addMouseListener(listener);
    }

    @Override
    public synchronized void removeMouseListener(MouseListener listener) {
        label.removeMouseListener(listener);
    }

    @Override
    public synchronized MouseListener[] getMouseListeners() {
        return label.getMouseListeners();
    }

    @Override
    public synchronized void addMouseMotionListener(MouseMotionListener listener) {
        label.addMouseMotionListener(listener);
    }

    @Override
    public synchronized void removeMouseMotionListener(MouseMotionListener listener) {
        label.removeMouseMotionListener(listener);
    }

    @Override
    public synchronized MouseMotionListener[] getMouseMotionListeners() {
        return label.getMouseMotionListeners();
    }
}
