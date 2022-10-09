package by.babanin.todo.view.component.tree;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Rectangle;
import java.util.EventObject;
import java.util.Optional;

import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.LookAndFeel;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreePath;

public class TreeTable extends JTable {

    private final transient TreeModelListener expandRootChildrenAutomaticallyListener = new ExpandRootChildrenAutomaticallyListener();

    private final TreeTableCellRenderer tree;

    private boolean treeEditable;
    private boolean allowShowIcons;
    private boolean expandRootChildrenAutomatically;

    public TreeTable() {
        // Create the tree. It will be used as a renderer and editor.
        tree = new TreeTableCellRenderer(this);
        tree.setShowsRootHandles(true);

        // Force the JTable and JTree to share their row selection models.
        ListToTreeSelectionModelWrapper selectionWrapper = new ListToTreeSelectionModelWrapper(
                row -> Optional.ofNullable(tree.getPathForRow(row)));
        tree.setSelectionModel(selectionWrapper);
        setSelectionModel(selectionWrapper.getListSelectionModel());

        // Install the tree editor renderer and editor.
        setDefaultRenderer(TreeTableModel.class, tree);
        setDefaultEditor(TreeTableModel.class, new TreeTableCellEditor(this));

        // Table row height and tree row height are the same
        tree.setRowHeight(getRowHeight());
    }

    public void setTreeTableModel(TreeTableModel treeTableModel) {
        tree.setModel(treeTableModel);
        // Install a tableModel representing the visible rows in the tree.
        setModel(new TreeTableModelAdapter(treeTableModel, tree));
        updateExpandRootChildrenAutomaticallyListener(treeTableModel);
    }

    public TreeTableModel getTreeTableModel() {
        return (TreeTableModel) tree.getModel();
    }

    /**
     * Returns the tree that is being shared between the model.
     */
    public JTree getTree() {
        return tree;
    }

    public boolean isTreeEditable() {
        return treeEditable;
    }

    public void setTreeEditable(boolean editable) {
        treeEditable = editable;
    }

    public boolean isAllowShowIcons() {
        return allowShowIcons;
    }

    public void setAllowShowIcons(boolean show) {
        allowShowIcons = show;
    }

    /**
     * Overridden to message super and forward the method to the tree.
     * Since the tree is not actually in the component hieachy it will
     * never receive this unless we forward it in this manner.
     */
    @Override
    public void updateUI() {
        super.updateUI();
        if(tree != null) {
            tree.updateUI();
            // Do this so that the editor is referencing the current renderer
            // from the tree. The renderer can potentially change each time
            // laf changes.
            setDefaultEditor(TreeTableModel.class, new TreeTableCellEditor(this));
        }
        // Use the tree's default foreground and background colors in the table.
        LookAndFeel.installColorsAndFont(this, "Tree.background",
                "Tree.foreground", "Tree.font");
    }

    /**
     * Workaround for BasicTableUI anomaly. Make sure the UI never tries to
     * resize the editor. The UI currently uses different techniques to
     * paint the renderers and editors and overriding setBounds() below
     * is not the right thing to do for an editor. Returning -1 for the
     * editing row in this case, ensures the editor is never painted.
     */
    @Override
    public int getEditingRow() {
        return isTreeColumn(editingColumn) ? -1 : editingRow;
    }

    /**
     * This is overriden to invoke supers implementation, and then,
     * if the receiver is editing a Tree column, the editors bounds is
     * reset. The reason we have to do this is because JTable doesn't
     * think the table is being edited, as <code>getEditingRow</code> returns
     * -1, and therefore doesn't automaticly resize the editor for us.
     */
    @Override
    public void sizeColumnsToFit(int resizingColumn) {
        super.sizeColumnsToFit(resizingColumn);
        if(editingColumn != -1 && isTreeColumn(editingColumn)) {
            Rectangle cellRect = getCellRect(editingRow, getEditingColumn(), false);
            Component component = getEditorComponent();
            component.setBounds(cellRect);
            component.validate();
        }
    }

    /**
     * Overridden to pass the new rowHeight to the tree.
     */
    @Override
    public void setRowHeight(int rowHeight) {
        super.setRowHeight(rowHeight);
        if(tree != null && tree.getRowHeight() != rowHeight) {
            tree.setRowHeight(getRowHeight());
        }
    }

    /**
     * Overriden to invoke repaint for the particular location if
     * the column contains the tree. This is done as the tree editor does
     * not fill the bounds of the cell, we need the renderer to paint
     * the tree in the background, and then draw the editor over it.
     */
    @Override
    public boolean editCellAt(int row, int column, EventObject e) {
        boolean retValue = super.editCellAt(row, column, e);
        if(retValue && isTreeColumn(column)) {
            repaint(getCellRect(row, column, false));
        }
        return retValue;
    }

    public boolean isTreeColumn(int column) {
        return getColumnClass(column).isAssignableFrom(TreeTableModel.class);
    }

    public void setRootVisible(boolean rootVisible) {
        tree.setRootVisible(rootVisible);
    }

    public void setExpandRootChildrenAutomatically(boolean expand) {
        this.expandRootChildrenAutomatically = expand;
        TreeTableModel treeTableModel = getTreeTableModel();
        if(treeTableModel != null) {
            updateExpandRootChildrenAutomaticallyListener(treeTableModel);
        }
    }

    private void updateExpandRootChildrenAutomaticallyListener(TreeTableModel treeTableModel) {
        if(expandRootChildrenAutomatically) {
            treeTableModel.addTreeModelListener(expandRootChildrenAutomaticallyListener);
        }
        else {
            treeTableModel.removeTreeModelListener(expandRootChildrenAutomaticallyListener);
        }
    }

    private class ExpandRootChildrenAutomaticallyListener implements TreeModelListener {
        @Override
        public void treeNodesChanged(TreeModelEvent event) {
            // do nothing
        }

        @Override
        public void treeNodesInserted(TreeModelEvent event) {
            EventQueue.invokeLater(() -> {
                TreeTableModel treeTableModel = getTreeTableModel();
                if(treeTableModel != null) {
                    Object root = treeTableModel.getRoot();
                    if(event.getTreePath().getLastPathComponent() == root) {
                        for(Object child : event.getChildren()) {
                            Object[] path = new Object[2];
                            path[0] = root;
                            path[1] = child;
                            tree.expandPath(new TreePath(path));
                        }
                    }
                }
            });
        }

        @Override
        public void treeNodesRemoved(TreeModelEvent event) {
            // do nothing
        }

        @Override
        public void treeStructureChanged(TreeModelEvent event) {
            EventQueue.invokeLater(() -> {
                TreeTableModel treeTableModel = getTreeTableModel();
                TreePath treePath = event.getTreePath();
                if(treeTableModel != null && treePath != null) {
                    Object root = treeTableModel.getRoot();
                    if(treePath.getLastPathComponent() == root) {
                        int childCount = treeTableModel.getChildCount(root);
                        for(int i = 0; i < childCount; i++) {
                            Object child = treeTableModel.getChild(root, i);
                            Object[] path = new Object[2];
                            path[0] = root;
                            path[1] = child;
                            tree.expandPath(new TreePath(path));
                        }
                    }
                }
            });
        }
    }
}
