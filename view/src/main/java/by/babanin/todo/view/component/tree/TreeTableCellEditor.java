package by.babanin.todo.view.component.tree;

import java.awt.Component;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.DefaultCellEditor;
import javax.swing.Icon;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 * An editor that can be used to edit the tree column. This extends
 * DefaultCellEditor and uses a JTextField (actually, TreeTableTextField)
 * to perform the actual editing.
 * <p>To support editing of the tree column we can not make the tree
 * editable. The reason this doesn't work is that you can not use
 * the same component for editing and renderering. The table may have
 * the need to paint cells, while a cell is being edited. If the same
 * component were used for the rendering and editing the component would
 * be moved around, and the contents would change. When editing, this
 * is undesirable, the contents of the text field must stay the same,
 * including the caret blinking, and selections persisting. For this
 * reason the editing is done via a TableCellEditor.
 * <p>Another interesting thing to be aware of is how tree positions
 * its render and editor. The render/editor is responsible for drawing the
 * icon indicating the type of node (leaf, branch...). The tree is
 * responsible for drawing any other indicators, perhaps an additional
 * +/- sign, or lines connecting the various nodes. So, the renderer
 * is positioned based on depth. On the other hand, table always makes
 * its editor fill the contents of the cell. To get the allusion
 * that the table cell editor is part of the tree, we don't want the
 * table cell editor to fill the cell bounds. We want it to be placed
 * in the same manner as tree places it editor, and have table message
 * the tree to paint any decorations the tree wants. Then, we would
 * only have to worry about the editing part. The approach taken
 * here is to determine where tree would place the editor, and to override
 * the <code>reshape</code> method in the JTextField component to
 * nudge the textfield to the location tree would place it. Since
 * JTreeTable will paint the tree behind the editor everything should
 * just work. So, that is what we are doing here. Determining of
 * the icon position will only work if the TreeCellRenderer is
 * an instance of DefaultTreeCellRenderer. If you need custom
 * TreeCellRenderers, that don't descend from DefaultTreeCellRenderer,
 * and you want to support editing in TreeTable, you will have
 * to do something similiar.
 */
public class TreeTableCellEditor extends DefaultCellEditor {

    private final TreeTable treeTable;

    public TreeTableCellEditor(TreeTable treeTable) {
        super(new TreeTableTextField());
        this.treeTable = treeTable;
    }

    /**
     * Overriden to determine an offset that tree would place the
     * editor at. The offset is determined from the
     * <code>getRowBounds</code> JTree method, and additionaly
     * from the icon DefaultTreeCellRenderer will use.
     * <p>The offset is then set on the TreeTableTextField component
     * created in the constructor, and returned.
     */
    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        Component component = super.getTableCellEditorComponent(table, value, isSelected, row, column);
        JTree tree = treeTable.getTree();
        int offsetRow = tree.isRootVisible() ? row : row - 1;
        int offset = tree.getRowBounds(offsetRow).x;
        if(tree.getCellRenderer() instanceof DefaultTreeCellRenderer treeCellRenderer) {
            offset += calculateIconOffset(treeCellRenderer, offsetRow);
        }
        if(getComponent() instanceof TreeTableTextField textField) {
            textField.setOffset(offset);
        }
        return component;
    }

    private int calculateIconOffset(DefaultTreeCellRenderer treeCellRenderer, int row) {
        JTree tree = treeTable.getTree();
        Object node = tree.getPathForRow(row).getLastPathComponent();
        Icon icon;
        if(tree.getModel().isLeaf(node)) {
            icon = treeCellRenderer.getLeafIcon();
        }
        else if(tree.isExpanded(row)) {
            icon = treeCellRenderer.getOpenIcon();
        }
        else {
            icon = treeCellRenderer.getClosedIcon();
        }
        int iconOffset = 0;
        if(icon != null) {
            iconOffset = treeCellRenderer.getIconTextGap() + icon.getIconWidth();
        }
        return iconOffset;
    }

    /**
     * This is overriden to forward the event to the tree. This will
     * return true if the click count >= 3, or the event is null.
     */
    @Override
    public boolean isCellEditable(EventObject event) {
        boolean cellEditable = event == null && treeTable.isTreeEditable();
        if(event instanceof MouseEvent mouseEvent) {
            cellEditable = mouseEvent.getClickCount() >= 3 && treeTable.isTreeEditable();
            // If the modifiers are not 0 (or the left mouse button),
            // tree may try and toggle the selection, and table
            // will then try and toggle, resulting in the
            // selection remaining the same. To avoid this, we
            // only dispatch when the modifiers are 0 (or the left mouse
            // button).
            if(shouldFireTreeMouseEvent(mouseEvent)) {
                for(int column = 0; column < treeTable.getColumnCount(); column++) {
                    if(treeTable.isTreeColumn(column)) {
                        fireTreeMouseEvent(mouseEvent, column);
                        break;
                    }
                }
            }
        }
        return cellEditable;
    }

    private static boolean shouldFireTreeMouseEvent(MouseEvent me) {
        return me.getModifiersEx() == 0 || me.getModifiersEx() == InputEvent.BUTTON1_DOWN_MASK;
    }

    private void fireTreeMouseEvent(MouseEvent mouseEvent, int column) {
        treeTable.getTree().dispatchEvent(createTreeMouseEvent(mouseEvent, column));
    }

    private MouseEvent createTreeMouseEvent(MouseEvent mouseEvent, int column) {
        return new MouseEvent(treeTable.getTree(), mouseEvent.getID(),
                        mouseEvent.getWhen(), mouseEvent.getModifiersEx(),
                        mouseEvent.getX() - treeTable.getCellRect(0, column, true).x,
                        mouseEvent.getY(), mouseEvent.getClickCount(),
                        mouseEvent.isPopupTrigger(), mouseEvent.getButton());
    }
}
