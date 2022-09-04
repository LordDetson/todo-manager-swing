package by.babanin.todo.view.component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.stream.IntStream;

import javax.swing.table.AbstractTableModel;

import by.babanin.todo.representation.ComponentRepresentation;
import by.babanin.todo.representation.ReportField;

public class TableModel<C> extends AbstractTableModel {

    private final List<C> list = new ArrayList<>();
    private final transient ComponentRepresentation<C> representation;
    private final transient List<ReportField> fields;

    public TableModel(ComponentRepresentation<C> representation, List<ReportField> fields) {
        this.representation = representation;
        this.fields = Collections.unmodifiableList(fields);
    }

    @Override
    public int getRowCount() {
        return list.size();
    }

    @Override
    public int getColumnCount() {
        return fields.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        C component = list.get(rowIndex);
        ReportField field = fields.get(columnIndex);
        return representation.getValueAt(component, field);
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return fields.get(columnIndex).getType();
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public void addAll(Collection<? extends C> collection) {
        int firstRow = list.size();
        list.addAll(collection);
        fireTableRowsInserted(firstRow, list.size());
    }

    public void add(C component) {
        addAll(Collections.singletonList(component));
    }

    public void add(int index, C component) {
        list.add(index, component);
        fireTableRowsInserted(index, index);
    }

    public void set(int index, C component) {
        list.set(index, component);
        fireTableRowsUpdated(index, index);
    }

    public C get(int row) {
        C component = null;
        if(row >= 0) {
            component = list.get(row);
        }
        return component;
    }

    public List<C> get(int[] indices) {
        return Arrays.stream(indices)
                .mapToObj(this::get)
                .toList();
    }

    public List<C> getAll() {
        return Collections.unmodifiableList(list);
    }

    protected List<C> getList() {
        return list;
    }

    public int indexOf(C component) {
        return list.indexOf(component);
    }

    public IntStream indexOf(Collection<C> components) {
        return components.stream()
                .mapToInt(list::indexOf);
    }

    public void remove(Collection<C> components) {
        IntStream intStream = indexOf(components);
        IntSummaryStatistics statistics = intStream.summaryStatistics();
        list.removeAll(components);
        fireTableRowsDeleted(statistics.getMin(), statistics.getMax());
    }

    public void clear() {
        list.clear();
    }
}
