package by.babanin.todo.view.component.tree;

import java.util.function.BiConsumer;

import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreePath;

/**
 * An abstract implementation of the TreeTableModel interface, handling the list of listeners.
 */
public abstract class AbstractTreeTableModel implements TreeTableModel {

    private final EventListenerList listenerList = new EventListenerList();

    private final Object root;

    protected AbstractTreeTableModel(Object root) {
        this.root = root;
    }

    @Override
    public Object getRoot() {
        return root;
    }

    @Override
    public boolean isLeaf(Object node) {
        return getChildCount(node) == 0;
    }

    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {
    }

    // This is not called in the JTree's default mode: use a naive implementation.
    @Override
    public int getIndexOfChild(Object parent, Object child) {
        for(int i = 0; i < getChildCount(parent); i++) {
            if(getChild(parent, i).equals(child)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * By default, make the column with the Tree in it the only editable one.
     * Making this column editable causes the JTable to forward mouse
     * and keyboard events in the Tree column to the underlying JTree.
     */
    @Override
    public boolean isCellEditable(Object node, int column) {
        return isTreeColumn(column);
    }

    public boolean isTreeColumn(int column) {
        return getColumnClass(column) == TreeTableModel.class;
    }

    @Override
    public void setValueAt(Object value, Object node, int column) {
    }

    @Override
    public void addTreeModelListener(TreeModelListener l) {
        listenerList.add(TreeModelListener.class, l);
    }

    @Override
    public void removeTreeModelListener(TreeModelListener l) {
        listenerList.remove(TreeModelListener.class, l);
    }

    protected void fireTreeNodesChanged(Object source, Object[] path, int[] childIndices, Object[] children) {
        fireTreeModelEvent(source, path, childIndices, children, TreeModelListener::treeNodesChanged);
    }

    protected void fireTreeNodesInserted(Object source, Object[] path, int[] childIndices, Object[] children) {
        fireTreeModelEvent(source, path, childIndices, children, TreeModelListener::treeNodesInserted);
    }

    protected void fireTreeNodesRemoved(Object source, Object[] path, int[] childIndices, Object[] children) {
        fireTreeModelEvent(source, path, childIndices, children, TreeModelListener::treeNodesRemoved);
    }

    protected void fireTreeStructureChanged(Object source, Object[] path, int[] childIndices, Object[] children) {
        fireTreeModelEvent(source, path, childIndices, children, TreeModelListener::treeStructureChanged);
    }

    protected void fireTreeModelEvent(Object source, Object[] path, int[] childIndices, Object[] children,
            BiConsumer<TreeModelListener, TreeModelEvent> consumer) {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        TreeModelEvent e = null;
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for(int i = listeners.length - 2; i >= 0; i -= 2) {
            if(listeners[i] == TreeModelListener.class) {
                // Lazily create the event:
                if(e == null) {
                    e = new TreeModelEvent(source, path, childIndices, children);
                }
                consumer.accept((TreeModelListener) listeners[i + 1], e);
            }
        }
    }
}
