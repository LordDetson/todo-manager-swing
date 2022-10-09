package by.babanin.todo.view.component.tree;

import javax.swing.JTextField;

/**
 * Component used by TreeTableCellEditor. The only thing this does
 * is to override the <code>reshape</code> method, and to ALWAYS
 * make the x location be <code>offset</code>.
 */
public class TreeTableTextField extends JTextField {

    private int offset;

    @Override
    public void setBounds(int x, int y, int w, int h) {
        int newX = Math.max(x, offset);
        super.setBounds(newX, y, w - (newX - x), h);
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }
}
