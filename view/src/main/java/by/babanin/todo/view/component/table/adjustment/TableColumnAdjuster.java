package by.babanin.todo.view.component.table.adjustment;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import javax.swing.AbstractAction;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
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

import by.babanin.todo.view.component.action.Action;
import by.babanin.todo.view.component.action.BindingAction;
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
    private final Set<String> columnIdsToFit = new HashSet<>();

    private final JTable table;
    private final int spacing;

    private final TableColumnAdjustment globalAdjustment;
    private TableColumnAdjustment adjustment = new TableColumnAdjustment();
    private boolean useGlobal = true;
    private int selectedColumnIndex = -1;
    private JMenu fitColumnsMenu;

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

        storeColumnSizes();

        for(int i = 0; i < tcm.getColumnCount(); i++) {
            adjustColumn(i, false);
        }
        fitColumns(false);
    }

    /**
     * Adjust the width of the specified column in the table
     */
    private void adjustColumn(final int column, boolean storeColumnSize) {
        TableColumn tableColumn = table.getColumnModel().getColumn(column);

        if(!tableColumn.getResizable()) {
            return;
        }
        if(columnIdsToFit.contains(tableColumn.getIdentifier().toString())) {
            return;
        }

        int columnHeaderWidth = getColumnHeaderWidth(column) + spacing;
        int columnDataWidth = getColumnContentWidth(column) + spacing;
        int preferredWidth = Math.max(columnHeaderWidth, columnDataWidth);

        updateTableColumn(column, preferredWidth, storeColumnSize);
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
    private void updateTableColumn(int column, int width, boolean storeColumnSize) {
        final TableColumn tableColumn = table.getColumnModel().getColumn(column);

        if(!tableColumn.getResizable()) {
            return;
        }

        // Don't shrink the column width
        if(adjustment.isOnlyAdjustLarger()) {
            width = Math.max(width, tableColumn.getPreferredWidth());
        }

        if(storeColumnSize) {
            storeColumnSize(tableColumn);
        }

        table.getTableHeader().setResizingColumn(tableColumn);
        tableColumn.setWidth(width);
    }

    private void storeColumnSizes() {
        Enumeration<TableColumn> columns = table.getColumnModel().getColumns();
        while(columns.hasMoreElements()) {
            storeColumnSize(columns.nextElement());
        }
    }

    private void storeColumnSize(TableColumn column) {
        columnSizes.put(column, column.getWidth());
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

    private void fitColumns(boolean storeColumnSize) {
        TableColumnModel columnModel = table.getColumnModel();
        for(String id : columnIdsToFit) {
            int columnIndex = columnModel.getColumnIndex(id);
            TableColumn column = columnModel.getColumn(columnIndex);
            int columnWidthToFit = getColumnWidthToFit(column);
            updateTableColumn(columnIndex, columnWidthToFit, storeColumnSize);
        }
    }

    /**
     * Get the column width to fill all the free width of the table
     * @param columnToFit is column to fit
     * @return the column width to fill
     */
    private int getColumnWidthToFit(TableColumn columnToFit) {
        TableColumnModel columnModel = table.getColumnModel();
        String columnId = columnToFit.getIdentifier().toString();
        if(!columnIdsToFit.contains(columnId)) {
            return 0;
        }

        int columnsWidthSum = 0;
        for(int i = 0; i < columnModel.getColumnCount(); i++) {
            TableColumn column = columnModel.getColumn(i);
            if(!columnIdsToFit.contains(column.getIdentifier().toString())) {
                columnsWidthSum += column.getWidth();
            }
        }
        return (table.getParent().getWidth() - columnsWidthSum) / columnIdsToFit.size();
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

    public Set<String> getColumnIdsToFit() {
        return columnIdsToFit;
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

        if("columnModel".equals(e.getPropertyName())) {
            columnIdsToFit.clear();
            fitColumnsMenu.removeAll();
            TableColumnModel columnModel = (TableColumnModel) e.getNewValue();
            Enumeration<TableColumn> columns = columnModel.getColumns();
            while(columns.hasMoreElements()) {
                TableColumn column = columns.nextElement();
                if(column.getResizable()) {
                    FitColumnToggleAction action = new FitColumnToggleAction(column);
                    fitColumnsMenu.add(new JCheckBoxMenuItem(action));
                }
            }
        }
    }

    @Override
    public void tableChanged(TableModelEvent e) {
        if(!isDynamicAdjustment()) {
            return;
        }

        // Needed when table is sorted.
        SwingUtilities.invokeLater(() -> {
            storeColumnSizes();
            // A cell has been updated
            int column = table.convertColumnIndexToView(e.getColumn());

            if(e.getType() == TableModelEvent.UPDATE && column != -1) {
                // Only need to worry about an increase in width for this cell
                if(isOnlyAdjustLarger()) {
                    int row = e.getFirstRow();
                    TableColumn tableColumn = table.getColumnModel().getColumn(column);

                    if(tableColumn.getResizable()) {
                        int width = getCellContentWidth(row, column) + spacing;
                        updateTableColumn(column, width, false);
                    }
                }
                // Could be an increase of decrease so check all rows
                else {
                    adjustColumn(column, false);
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
        Supplier<BindingAction> actionFactory = () -> new BindingAction(table);
        Action columnsHeaderIncludedToggleAction = Action.builder(actionFactory)
                .name(Translator.toLocale(TranslateCode.SETTINGS_COLUMNS_HEADER_INCLUDED))
                .action(actionEvent -> {
                    setColumnHeaderIncluded(!isColumnHeaderIncluded());
                    adjustColumns();
                })
                .build();

        Action columnsContentIncludedToggleAction = Action.builder(actionFactory)
                .name(Translator.toLocale(TranslateCode.SETTINGS_COLUMNS_CONTENT_INCLUDED))
                .action(actionEvent -> {
                    setColumnContentIncluded(!isColumnContentIncluded());
                    adjustColumns();
                })
                .build();

        Action onlyAdjustLargerToggleAction = Action.builder(actionFactory)
                .id("toggleLarger")
                .name(Translator.toLocale(TranslateCode.SETTINGS_ONLY_ADJUST_LARGER))
                .accelerator(KeyStroke.getKeyStroke("control shift L"))
                .action(actionEvent -> {
                    setOnlyAdjustLarger(!isOnlyAdjustLarger());
                    adjustColumns();
                })
                .build();

        Action dynamicAdjustmentToggleAction = Action.builder(actionFactory)
                .id("toggleDynamic")
                .name(Translator.toLocale(TranslateCode.SETTINGS_DYNAMIC_ADJUSTMENT))
                .accelerator(KeyStroke.getKeyStroke("control shift D"))
                .action(actionEvent -> {
                    setDynamicAdjustment(!isDynamicAdjustment());
                    adjustColumns();
                })
                .build();

        Action useGlobalToggleAction = Action.builder(actionFactory)
                .name(Translator.toLocale(TranslateCode.SETTINGS_USE_GLOBAL))
                .action(actionEvent -> {
                    useGlobal = !useGlobal;
                    adjustColumns();
                })
                .build();

        Action adjustTableColumnsAction = Action.builder(actionFactory)
                .id("adjustTableColumns")
                .name(Translator.toLocale(TranslateCode.SETTINGS_ADJUST_COLUMNS))
                .accelerator(KeyStroke.getKeyStroke("control shift T"))
                .action(actionEvent -> adjustColumns())
                .build();

        Action adjustSelectedTableColumnAction = Action.builder(actionFactory)
                .id("adjustSelectedTableColumn")
                .name(Translator.toLocale(TranslateCode.SETTINGS_ADJUST_SELECTED_COLUMN))
                .action(actionEvent -> {
                    if(selectedColumnIndex < 0) {
                        selectedColumnIndex = getSelectedColumnIndex();
                    }
                    if(selectedColumnIndex > -1) {
                        adjustColumn(selectedColumnIndex, true);
                        selectedColumnIndex = -1;
                    }
                })
                .build();

        Action restoreTableColumnsAction = Action.builder(actionFactory)
                .id("restoreTableColumns")
                .name(Translator.toLocale(TranslateCode.SETTINGS_RESTORE_COLUMNS))
                .accelerator(KeyStroke.getKeyStroke("control shift R"))
                .action(actionEvent -> restoreColumns())
                .build();

        Action restoreSelectedTableColumnAction = Action.builder(actionFactory)
                .id("restoreSelectedTableColumn")
                .name(Translator.toLocale(TranslateCode.SETTINGS_RESTORE_SELECTED_COLUMN))
                .action(actionEvent -> {
                    if(selectedColumnIndex < 0) {
                        selectedColumnIndex = getSelectedColumnIndex();
                    }
                    if(selectedColumnIndex > -1) {
                        restoreColumn(selectedColumnIndex);
                        selectedColumnIndex = -1;
                    }
                })
                .build();

        fitColumnsMenu = new JMenu(Translator.toLocale(TranslateCode.SETTINGS_COLUMNS_TO_FIT));
        JCheckBoxMenuItem columnHeaderIncludedMenuItem = new JCheckBoxMenuItem(columnsHeaderIncludedToggleAction);
        JCheckBoxMenuItem columnContentIncludedMenuItem = new JCheckBoxMenuItem(columnsContentIncludedToggleAction);
        JCheckBoxMenuItem onlyAdjustLargerMenuItem = new JCheckBoxMenuItem(onlyAdjustLargerToggleAction);
        JCheckBoxMenuItem dynamicAdjustmentMenuItem = new JCheckBoxMenuItem(dynamicAdjustmentToggleAction);
        JCheckBoxMenuItem useGlobalMenuItem = new JCheckBoxMenuItem(useGlobalToggleAction);
        JPopupMenu popupMenu = new JPopupMenu();
        popupMenu.add(fitColumnsMenu);
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

                for(int i = 0; i < fitColumnsMenu.getItemCount(); i++) {
                    if(fitColumnsMenu.getItem(i) instanceof JCheckBoxMenuItem checkBoxMenuItem &&
                            checkBoxMenuItem.getAction() instanceof FitColumnToggleAction fitColumnToggleAction) {
                        checkBoxMenuItem.setState(columnIdsToFit.contains(fitColumnToggleAction.getColumnId()));
                    }
                }
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

    public int getSelectedColumnIndex() {
        int selectedColumn = table.getSelectedColumn();
        if(selectedColumn < 0) {
            selectedColumn = table.columnAtPoint(GUIUtils.getMouseLocationOn(table));
        }
        return selectedColumn;
    }

    private final class FitColumnToggleAction extends AbstractAction {

        private final TableColumn column;

        private FitColumnToggleAction(TableColumn column) {
            super(column.getHeaderValue().toString());
            this.column = column;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String id = getColumnId();
            if(columnIdsToFit.contains(id)) {
                columnIdsToFit.remove(id);
            }
            else {
                columnIdsToFit.add(id);
            }
            adjustColumns();
        }

        public String getColumnId() {
            return column.getIdentifier().toString();
        }
    }

}
