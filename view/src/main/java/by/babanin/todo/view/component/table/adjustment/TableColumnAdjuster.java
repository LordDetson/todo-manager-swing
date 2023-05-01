package by.babanin.todo.view.component.table.adjustment;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import by.babanin.todo.view.exception.ViewException;
import by.babanin.todo.view.translat.TranslateCode;
import by.babanin.todo.view.translat.Translator;
import by.babanin.todo.view.util.GUIUtils;

public class TableColumnAdjuster implements PropertyChangeListener, TableModelListener {

    /**
     * Sometimes the calculated width is too close to the value width, causing 3 dots to appear.
     * To avoid this, extra space is added.
     */
    private static final int DEFAULT_SPACING = 3;

    private final Map<TableColumn, Integer> columnSizes = new HashMap<>();

    private final JTable table;
    private final int spacing;

    private final TableColumnAdjustment globalAdjustment;
    private TableColumnAdjustment adjustment = new TableColumnAdjustment();
    private boolean useGlobal = true;
    private int selectedColumnIndex = -1;

    /**
     * Specify the table and use default spacing
     */
    public TableColumnAdjuster(JTable table, TableColumnAdjustment globalAdjustment) {
        this(table, globalAdjustment, DEFAULT_SPACING);
    }

    /**
     * Specify the table and spacing
     */
    public TableColumnAdjuster(JTable table, TableColumnAdjustment globalAdjustment, int spacing) {
        this.table = table;
        this.globalAdjustment = globalAdjustment;
        this.spacing = spacing;
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.addPropertyChangeListener(this);
        table.getModel().addTableModelListener(this);
        installActions();
    }

    public TableColumnAdjustment getAdjustment() {
        return adjustment;
    }

    public void setAdjustment(TableColumnAdjustment adjustment) {
        this.adjustment = adjustment.clone();
        this.useGlobal = false;
    }

    /**
     * Adjust the widths of all the columns in the table
     */
    public void adjustColumns() {
        if(useGlobal) {
            adjustment = globalAdjustment.clone();
        }
        TableColumnModel tcm = table.getColumnModel();

        for(int i = 0; i < tcm.getColumnCount(); i++) {
            adjustColumn(i);
        }
    }

    /**
     * Adjust the width of the specified column in the table
     */
    public void adjustColumn(final int column) {
        TableColumn tableColumn = table.getColumnModel().getColumn(column);

        if(!tableColumn.getResizable()) {
            return;
        }

        int columnHeaderWidth = getColumnHeaderWidth(column);
        int columnDataWidth = getColumnContentWidth(column);
        int preferredWidth = Math.max(columnHeaderWidth, columnDataWidth);

        updateTableColumn(column, preferredWidth);
    }

    /**
     * Calculated the width based on the column name
     */
    private int getColumnHeaderWidth(int column) {
        if(!adjustment.isColumnHeaderIncluded()) {
            return 0;
        }

        TableColumn tableColumn = table.getColumnModel().getColumn(column);
        Object value = tableColumn.getHeaderValue();
        TableCellRenderer renderer = tableColumn.getHeaderRenderer();

        if(renderer == null) {
            renderer = table.getTableHeader().getDefaultRenderer();
        }

        Component c = renderer.getTableCellRendererComponent(table, value, false, false, -1, column);
        return c.getPreferredSize().width;
    }

    /**
     * Calculate the width based on the widest cell renderer for the
     * given column.
     */
    private int getColumnContentWidth(int column) {
        if(!adjustment.isColumnContentIncluded()) {
            return 0;
        }

        int preferredWidth = 0;
        int maxWidth = table.getColumnModel().getColumn(column).getMaxWidth();

        for(int row = 0; row < table.getRowCount(); row++) {
            preferredWidth = Math.max(preferredWidth, getCellContentWidth(row, column));
            // We've exceeded the maximum width, no need to check other rows
            if(preferredWidth >= maxWidth) {
                break;
            }
        }

        return preferredWidth;
    }

    /**
     * Get the preferred width for the specified cell
     */
    private int getCellContentWidth(int row, int column) {
        // Invoke the renderer for the cell to calculate the preferred width
        TableCellRenderer cellRenderer = table.getCellRenderer(row, column);
        Component c = table.prepareRenderer(cellRenderer, row, column);
        return c.getPreferredSize().width + table.getIntercellSpacing().width;
    }

    /**
     * Update the TableColumn with the newly calculated width
     */
    private void updateTableColumn(int column, int width) {
        final TableColumn tableColumn = table.getColumnModel().getColumn(column);

        if(!tableColumn.getResizable()) {
            return;
        }

        width += spacing;

        // Don't shrink the column width
        if(adjustment.isOnlyAdjustLarger()) {
            width = Math.max(width, tableColumn.getPreferredWidth());
        }

        columnSizes.put(tableColumn, tableColumn.getWidth());

        table.getTableHeader().setResizingColumn(tableColumn);
        tableColumn.setWidth(width);
    }

    /**
     * Restore the widths of the columns in the table to its previous width
     */
    public void restoreColumns() {
        TableColumnModel tcm = table.getColumnModel();

        for(int i = 0; i < tcm.getColumnCount(); i++) {
            restoreColumn(i);
        }
    }

    /**
     * Restore the width of the specified column to its previous width
     */
    private void restoreColumn(int column) {
        TableColumn tableColumn = table.getColumnModel().getColumn(column);
        Integer width = columnSizes.get(tableColumn);

        if(width != null) {
            table.getTableHeader().setResizingColumn(tableColumn);
            tableColumn.setWidth(width);
        }
    }

    /**
     * Indicates whether to include the header in the width calculation
     */
    public void setColumnHeaderIncluded(boolean columnHeaderIncluded) {
        adjustment.setColumnHeaderIncluded(columnHeaderIncluded);
        useGlobal = false;
    }

    /**
     * Indicates whether to include the model data in the width calculation
     */
    public void setColumnContentIncluded(boolean columnDataIncluded) {
        adjustment.setColumnContentIncluded(columnDataIncluded);
        useGlobal = false;
    }

    /**
     * Indicates whether columns can only be increased in size
     */
    public void setOnlyAdjustLarger(boolean onlyAdjustLarger) {
        adjustment.setOnlyAdjustLarger(onlyAdjustLarger);
        useGlobal = false;
    }

    /**
     * Indicate whether changes to the model should cause the width to be
     * dynamically recalculated.
     */
    public void setDynamicAdjustment(boolean dynamicAdjustment) {
        adjustment.setDynamicAdjustment(dynamicAdjustment);
        useGlobal = false;
    }

    public void setUseGlobal(boolean useGlobal) {
        this.useGlobal = useGlobal;
    }

    public boolean isColumnHeaderIncluded() {
        return adjustment.isColumnHeaderIncluded();
    }

    public boolean isColumnContentIncluded() {
        return adjustment.isColumnContentIncluded();
    }

    public boolean isOnlyAdjustLarger() {
        return adjustment.isOnlyAdjustLarger();
    }

    public boolean isDynamicAdjustment() {
        return adjustment.isDynamicAdjustment();
    }

    public boolean isUseGlobal() {
        return useGlobal;
    }

    @Override
    public void propertyChange(PropertyChangeEvent e) {
        // When the TableModel changes we need to update the listeners and column widths
        if("model".equals(e.getPropertyName())) {
            AbstractTableModel model = (AbstractTableModel) e.getOldValue();
            model.removeTableModelListener(this);

            model = (AbstractTableModel) e.getNewValue();
            model.addTableModelListener(this);
            adjustColumns();
        }
    }

    @Override
    public void tableChanged(TableModelEvent e) {
        if(!isDynamicAdjustment()) {
            return;
        }

        // Needed when table is sorted.
        SwingUtilities.invokeLater(() -> {
            // A cell has been updated
            int column = table.convertColumnIndexToView(e.getColumn());

            if(e.getType() == TableModelEvent.UPDATE && column != -1) {
                // Only need to worry about an increase in width for this cell
                if(isOnlyAdjustLarger()) {
                    int row = e.getFirstRow();
                    TableColumn tableColumn = table.getColumnModel().getColumn(column);

                    if(tableColumn.getResizable()) {
                        int width = getCellContentWidth(row, column);
                        updateTableColumn(column, width);
                    }
                }
                // Could be an increase of decrease so check all rows
                else {
                    adjustColumn(column);
                }
            }
            // The update affected more than one column so adjust all columns
            else {
                adjustColumns();
            }
        });
    }

    /**
     * Install Actions to give user control of certain functionality.
     */
    private void installActions() {
        ColumnsHeaderIncludedToggleAction columnsHeaderIncludedToggleAction = new ColumnsHeaderIncludedToggleAction();
        ColumnsContentIncludedToggleAction columnsContentIncludedToggleAction = new ColumnsContentIncludedToggleAction();
        OnlyAdjustLargerToggleAction onlyAdjustLargerToggleAction = new OnlyAdjustLargerToggleAction();
        DynamicAdjustmentToggleAction dynamicAdjustmentToggleAction = new DynamicAdjustmentToggleAction();
        UseGlobalToggleAction useGlobalToggleAction = new UseGlobalToggleAction();
        AdjustTableColumnsAction adjustTableColumnsAction = new AdjustTableColumnsAction();
        AdjustSelectedTableColumnAction adjustSelectedTableColumnAction = new AdjustSelectedTableColumnAction();
        RestoreTableColumnsAction restoreTableColumnsAction = new RestoreTableColumnsAction();
        RestoreSelectedTableColumnAction restoreSelectedTableColumnAction = new RestoreSelectedTableColumnAction();

        JCheckBoxMenuItem columnHeaderIncludedMenuItem = new JCheckBoxMenuItem(columnsHeaderIncludedToggleAction);
        JCheckBoxMenuItem columnContentIncludedMenuItem = new JCheckBoxMenuItem(columnsContentIncludedToggleAction);
        JCheckBoxMenuItem onlyAdjustLargerMenuItem = new JCheckBoxMenuItem(onlyAdjustLargerToggleAction);
        JCheckBoxMenuItem dynamicAdjustmentMenuItem = new JCheckBoxMenuItem(dynamicAdjustmentToggleAction);
        JCheckBoxMenuItem useGlobalMenuItem = new JCheckBoxMenuItem(useGlobalToggleAction);
        JPopupMenu popupMenu = new JPopupMenu();
        popupMenu.add(columnHeaderIncludedMenuItem);
        popupMenu.add(columnContentIncludedMenuItem);
        popupMenu.add(onlyAdjustLargerMenuItem);
        popupMenu.add(dynamicAdjustmentMenuItem);
        popupMenu.add(useGlobalMenuItem);
        popupMenu.addSeparator();
        popupMenu.add(adjustTableColumnsAction);
        popupMenu.add(adjustSelectedTableColumnAction);
        popupMenu.add(restoreTableColumnsAction);
        popupMenu.add(restoreSelectedTableColumnAction);
        popupMenu.addPopupMenuListener(new PopupMenuListener() {

            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                columnHeaderIncludedMenuItem.setState(isColumnHeaderIncluded());
                columnContentIncludedMenuItem.setState(isColumnContentIncluded());
                onlyAdjustLargerMenuItem.setState(isOnlyAdjustLarger());
                dynamicAdjustmentMenuItem.setState(isDynamicAdjustment());
                useGlobalMenuItem.setState(useGlobal);
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                // do nothing
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
                selectedColumnIndex = -1;
            }
        });
        popupMenu.addPropertyChangeListener("visible", e -> {
            if(e.getNewValue().equals(Boolean.TRUE)) {
                selectedColumnIndex = table.columnAtPoint(GUIUtils.getPopupMenuLocationOnInvoker(popupMenu));
            }
            else {
                EventQueue.invokeLater(() -> selectedColumnIndex = -1);
            }
        });
        table.getTableHeader().setComponentPopupMenu(popupMenu);
    }

    private final class ColumnsHeaderIncludedToggleAction extends TableAction {

        public ColumnsHeaderIncludedToggleAction() {
            super(Translator.toLocale(TranslateCode.SETTINGS_COLUMNS_HEADER_INCLUDED));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            setColumnHeaderIncluded(!isColumnHeaderIncluded());
            adjustColumns();
        }
    }

    private final class ColumnsContentIncludedToggleAction extends TableAction {

        public ColumnsContentIncludedToggleAction() {
            super(Translator.toLocale(TranslateCode.SETTINGS_COLUMNS_CONTENT_INCLUDED));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            setColumnContentIncluded(!isColumnContentIncluded());
            adjustColumns();
        }
    }

    private final class OnlyAdjustLargerToggleAction extends TableAction {

        public OnlyAdjustLargerToggleAction() {
            super(Translator.toLocale(TranslateCode.SETTINGS_ONLY_ADJUST_LARGER));
            setId("toggleLarger");
            setAccelerator(KeyStroke.getKeyStroke("control MULTIPLY"));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            setOnlyAdjustLarger(!isOnlyAdjustLarger());
            adjustColumns();
        }
    }

    private final class DynamicAdjustmentToggleAction extends TableAction {

        public DynamicAdjustmentToggleAction() {
            super(Translator.toLocale(TranslateCode.SETTINGS_DYNAMIC_ADJUSTMENT));
            setId("toggleDynamic");
            setAccelerator(KeyStroke.getKeyStroke("control DIVIDE"));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            setDynamicAdjustment(!isDynamicAdjustment());
            adjustColumns();
        }
    }

    private class UseGlobalToggleAction extends TableAction {

        public UseGlobalToggleAction() {
            super(Translator.toLocale(TranslateCode.SETTINGS_USE_GLOBAL));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            useGlobal = !useGlobal;
            adjustColumns();
        }
    }

    private class AdjustTableColumnsAction extends TableAction {

        public AdjustTableColumnsAction() {
            super(Translator.toLocale(TranslateCode.SETTINGS_ADJUST_COLUMNS));
            setId("adjustTableColumns");
            setAccelerator(KeyStroke.getKeyStroke("control ADD"));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            adjustColumns();
        }
    }

    private class AdjustSelectedTableColumnAction extends TableAction {

        public AdjustSelectedTableColumnAction() {
            super(Translator.toLocale(TranslateCode.SETTINGS_ADJUST_SELECTED_COLUMN));
            setId("adjustSelectedTableColumn");
            setAccelerator(KeyStroke.getKeyStroke("control shift ADD"));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if(selectedColumnIndex < 0) {
                selectedColumnIndex = getSelectedColumnIndex();
            }
            if(selectedColumnIndex > -1) {
                adjustColumn(selectedColumnIndex);
                selectedColumnIndex = -1;
            }
        }
    }

    private class RestoreTableColumnsAction extends TableAction {

        public RestoreTableColumnsAction() {
            super(Translator.toLocale(TranslateCode.SETTINGS_RESTORE_COLUMNS));
            setId("restoreTableColumns");
            setAccelerator(KeyStroke.getKeyStroke("control SUBTRACT"));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            restoreColumns();
        }
    }

    private class RestoreSelectedTableColumnAction extends TableAction {

        public RestoreSelectedTableColumnAction() {
            super(Translator.toLocale(TranslateCode.SETTINGS_RESTORE_SELECTED_COLUMN));
            setId("restoreSelectedTableColumn");
            setAccelerator(KeyStroke.getKeyStroke("control shift SUBTRACT"));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if(selectedColumnIndex < 0) {
                selectedColumnIndex = getSelectedColumnIndex();
            }
            if(selectedColumnIndex > -1) {
                restoreColumn(selectedColumnIndex);
                selectedColumnIndex = -1;
            }
        }
    }

    private abstract class TableAction extends AbstractAction {

        public static final String ID_KEY = "id";

        protected TableAction(String name) {
            super(name);
            addPropertyChangeListener(event -> {
                String propertyName = event.getPropertyName();
                if(propertyName.equals(Action.ACCELERATOR_KEY)) {
                    String id = getId();
                    table.getInputMap().put(getAccelerator(), id);
                    table.getActionMap().put(id, TableAction.this);
                }
            });
        }

        public String getId() {
            return (String) Optional.ofNullable(getValue(ID_KEY))
                    .orElseThrow(() -> new ViewException("ID is null"));
        }

        public void setId(String id) {
            putValue(ID_KEY, id);
        }

        public KeyStroke getAccelerator() {
            return (KeyStroke) Optional.ofNullable(getValue(Action.ACCELERATOR_KEY))
                    .orElseThrow(() -> new ViewException("Accelerator is null"));
        }

        public void setAccelerator(KeyStroke accelerator) {
            putValue(Action.ACCELERATOR_KEY, accelerator);
        }

        public int getSelectedColumnIndex() {
            int selectedColumn = table.getSelectedColumn();
            if(selectedColumn < 0) {
                selectedColumn = table.columnAtPoint(GUIUtils.getMouseLocationOn(table));
            }
            return selectedColumn;
        }
    }
}
