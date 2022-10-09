package by.babanin.todo.view.component.tree;

import javax.swing.tree.TreeModel;

/**
 * TreeTableModel is the model used by a TreeTable. It extends TreeModel
 * to add methods for getting inforamtion about the set of columns each
 * node in the TreeTableModel may have. Each column, like a column in
 * a TableModel, has a name and a type associated with it. Each node in
 * the TreeTableModel can return a value for each of the columns and
 * set that value if isCellEditable() returns true.
 */
public interface TreeTableModel extends TreeModel {

    /**
     * Returns the number ofs availible column.
     */
    int getColumnCount();

    /**
     * Returns the name for column number <code>column</code>.
     */
    String getColumnName(int column);

    /**
     * Returns the type for column number <code>column</code>.
     */
    Class<?> getColumnClass(int column);

    /**
     * Returns the value to be displayed for node <code>node</code>,
     * at column number <code>column</code>.
     */
    Object getValueAt(Object node, int column);

    /**
     * Indicates whether the value for node <code>node</code>,
     * at column number <code>column</code> is editable.
     */
    boolean isCellEditable(Object node, int column);

    /**
     * Sets the value for node <code>node</code>,
     * at column number <code>column</code>.
     */
    void setValueAt(Object value, Object node, int column);

    int getTreeColumn();
}
