package by.babanin.todo.view.component.tree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.IntStream;

import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreePath;

import by.babanin.todo.representation.ComponentRepresentation;
import by.babanin.todo.representation.ReportField;
import by.babanin.todo.view.component.DynamicTableModel;
import by.babanin.todo.view.component.ListTableModel;

public abstract class DynamicTreeTableModel<C, N extends Node<C, N>> implements TreeTableModel, ListTableModel<C> {

    private final EventListenerList listenerList = new EventListenerList();
    private final List<ReportField> groupByFields = new ArrayList<>();

    private final DynamicTableModel<C> model;

    private N root;

    protected DynamicTreeTableModel(ComponentRepresentation<C> representation) {
        this.model = new DynamicTableModel<>(representation);
    }

    @Override
    public void addAll(Collection<C> components) {
        if(!components.isEmpty() && root == null) {
            root = createRoot();
            for(C component : components) {
                N parent = root;
                for(ReportField field : groupByFields) {
                    Object value = field.getValue(component);
                    Optional<N> foundNode = findNode(parent, value);
                    if(foundNode.isPresent()) {
                        parent = foundNode.get();
                    }
                    else {
                        parent = createNode(parent, value);
                    }
                }
                createNode(parent, component);
            }
            fireTreeStructureChanged(root.getPath().toArray(), null, null);
        }
        else {
            /*List<N> allChildren = root.getChildrenRecursively();
            Map<N, Map<Integer, N>> newChildrenMap = new LinkedHashMap<>();
            components.forEach(component -> {
                N parent = allChildren.stream()
                        .filter(n -> nodeBuildStrategy.isChild(n.getComponent(), component))
                        .findFirst()
                        .orElseThrow(() -> new ViewException("Can't find parent for " + component));
                N child = nodeBuildStrategy.createNode(parent, component);
                parent.addChild(child);
                newChildrenMap.computeIfAbsent(parent, o -> new LinkedHashMap<>())
                        .put(parent.getChildren().size() - 1, child);
            });
            newChildrenMap.forEach((parent, children) -> {
                Set<Integer> integers = children.keySet();
                int[] array = new int[integers.size()];
                int i = 0;
                for(int index : integers) {
                    array[i] = index;
                }
                fireTreeNodesInserted(parent.getPath().toArray(), array, children.keySet().toArray());
            });*/
        }
        model.addAll(components);
    }

    protected abstract N createRoot();

    protected abstract N createNode(N parent, Object value);

    protected abstract Optional<N> findNode(N parent, Object value);

    public void setGroupByFields(List<ReportField> fields) {
        groupByFields.clear();
        root = null;
        groupByFields.addAll(fields);
        addAll(model.getAll());
    }

    public void addGroupByFields(List<ReportField> fields) {
        root = null;
        groupByFields.addAll(fields);
        addAll(model.getAll());
    }

    public void addGroupByField(ReportField field) {
        root = null;
        groupByFields.add(field);
        addAll(model.getAll());
    }

    public void removeGroupByFields(List<ReportField> fields) {
        root = null;
        groupByFields.removeAll(fields);
        addAll(model.getAll());
    }

    public void removeGroupByField(ReportField field) {
        root = null;
        groupByFields.remove(field);
        addAll(model.getAll());
    }

    @Override
    public void add(int index, C component) {

    }

    @Override
    public void set(int index, C component) {

    }

    @Override
    public C get(int row) {
        return null;
    }

    @Override
    public List<C> getAll() {
        return model.getAll();
    }

    @Override
    public int indexOf(C component) {
        return 0;
    }

    @Override
    public IntStream indexOf(Collection<C> components) {
        return null;
    }

    @Override
    public void remove(Collection<C> components) {

    }

    @Override
    public void clear() {
        root = null;
        groupByFields.clear();
        model.clear();
        fireTreeStructureChanged(null, null, null);
    }

    @Override
    public int size() {
        return root.getChildrenRecursively().size();
    }

    @Override
    public int getColumnCount() {
        return model.getColumnCount();
    }

    @Override
    public String getColumnName(int column) {
        return model.getColumnName(column);
    }

    @Override
    public Class<?> getColumnClass(int column) {
        if(getTreeColumn() == column) {
            return TreeTableModel.class;
        }
        return model.getColumnClass(column);
    }

    @Override
    public boolean isCellEditable(Object node, int column) {
        return isTreeColumn(column);
    }

    public boolean isTreeColumn(int column) {
        return getTreeColumn() == column && getColumnClass(column) == TreeTableModel.class;
    }

    @Override
    public void setValueAt(Object value, Object node, int column) {
        N aNode = (N) node;
        C component = aNode.getComponent();
        model.setValueAt(value, model.indexOf(component), column);
    }

    @Override
    public int getTreeColumn() {
        return 0;
    }

    @Override
    public Object getRoot() {
        return root;
    }

    @Override
    public Object getChild(Object parent, int index) {
        N aParent = (N) parent;
        return aParent.getChildren().get(index);
    }

    @Override
    public int getChildCount(Object parent) {
        N aParent = (N) parent;
        return aParent.getChildren().size();
    }

    @Override
    public boolean isLeaf(Object node) {
        return getChildCount(node) == 0;
    }

    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {

    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        for(int i = 0; i < getChildCount(parent); i++) {
            if(getChild(parent, i).equals(child)) {
                return i;
            }
        }
        return -1;
    }

    protected DynamicTableModel<C> getModel() {
        return model;
    }

    @Override
    public void addTreeModelListener(TreeModelListener l) {
        listenerList.add(TreeModelListener.class, l);
    }

    @Override
    public void removeTreeModelListener(TreeModelListener l) {
        listenerList.remove(TreeModelListener.class, l);
    }

    protected void fireTreeNodesChanged(Object[] path, int[] childIndices, Object[] children) {
        fireTreeModelEvent(path, childIndices, children, TreeModelListener::treeNodesChanged);
    }

    protected void fireTreeNodesInserted(Object[] path, int[] childIndices, Object[] children) {
        fireTreeModelEvent(path, childIndices, children, TreeModelListener::treeNodesInserted);
    }

    protected void fireTreeNodesRemoved(Object[] path, int[] childIndices, Object[] children) {
        fireTreeModelEvent(path, childIndices, children, TreeModelListener::treeNodesRemoved);
    }

    protected void fireTreeStructureChanged(Object[] path, int[] childIndices, Object[] children) {
        fireTreeModelEvent(path, childIndices, children, TreeModelListener::treeStructureChanged);
    }

    protected void fireTreeModelEvent(Object[] path, int[] childIndices, Object[] children,
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
                    e = new TreeModelEvent(this, path, childIndices, children);
                }
                consumer.accept((TreeModelListener) listeners[i + 1], e);
            }
        }
    }
}
