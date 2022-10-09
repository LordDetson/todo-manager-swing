package by.babanin.todo.view.component.tree;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.DefaultTreeCellRenderer;

public class TreeTableCellRenderer extends JTree implements TableCellRenderer {

    private final TreeTable treeTable;

    /**
     * Last table/tree row asked to renderer.
     */
    private int visibleRow;

    /**
     * Border to draw around the tree, if this is non-null, it will be painted.
     */
    private transient Border highlightBorder;

    public TreeTableCellRenderer(TreeTable treeTable) {
        this.treeTable = treeTable;
        setCellRenderer(new TreeCellRenderer(treeTable::getTreeTableModel, treeTable::isAllowShowIcons));
    }

    /**
     * updateUI is overridden to set the colors of the Tree's renderer
     * to match that of the table.
     */
    @Override
    public void updateUI() {
        super.updateUI();
        // Make the tree's cell renderer use the table's cell selection colors.
        if(cellRenderer instanceof DefaultTreeCellRenderer treeCellRenderer) {
            treeCellRenderer.setTextSelectionColor(UIManager.getColor("Table.selectionForeground"));
            treeCellRenderer.setBackgroundSelectionColor(UIManager.getColor("Table.selectionBackground"));
        }
    }

    /**
     * Sets the row height of the tree, and forwards the row height to the table.
     */
    @Override
    public void setRowHeight(int rowHeight) {
        if(rowHeight > 0) {
            super.setRowHeight(rowHeight);
            if(treeTable.getRowHeight() != rowHeight) {
                treeTable.setRowHeight(getRowHeight());
            }
        }
    }

    /**
     * This is overridden to set the height to match that of the JTable.
     */
    @Override
    public void setBounds(int x, int y, int w, int h) {
        super.setBounds(x, 0, w, treeTable.getHeight());
    }

    /**
     * Sublcassed to translate the graphics such that the last visible row will be drawn at 0,0.
     */
    @Override
    public void paint(Graphics g) {
        g.translate(0, -visibleRow * getRowHeight() - 1);
        super.paint(g);
        // Draw the Table border if we have focus.
        if(highlightBorder != null) {
            highlightBorder.paintBorder(this, g, 0, visibleRow * getRowHeight() + 1, getWidth(), getRowHeight());
        }
    }

    /**
     * TreeCellRenderer method. Overridden to update the visible row.
     */
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        visibleRow = row;
        updateBorder(hasFocus);
        updateBackgroundAndForeground(isSelected);
        return this;
    }

    private void updateBorder(boolean hasFocus) {
        highlightBorder = null;
        if(hasFocus) {
            highlightBorder = UIManager.getBorder("Table.focusCellHighlightBorder");
        }
    }

    private void updateBackgroundAndForeground(boolean isSelected) {
        Color background = isSelected ? treeTable.getSelectionBackground() : treeTable.getBackground();
        setBackground(background);
        if(cellRenderer instanceof DefaultTreeCellRenderer treeCellRenderer) {
            if(isSelected) {
                treeCellRenderer.setBackgroundSelectionColor(background);
                treeCellRenderer.setTextSelectionColor(treeTable.getSelectionForeground());
            }
            else {
                treeCellRenderer.setBackgroundNonSelectionColor(background);
                treeCellRenderer.setTextNonSelectionColor(treeTable.getForeground());
            }
        }
    }
}
