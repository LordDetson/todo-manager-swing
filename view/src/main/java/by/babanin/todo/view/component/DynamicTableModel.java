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

public class DynamicTableModel<C> extends AbstractTableModel implements ListTableModel<C> {

    private final List<C> list = new ArrayList<>();
    private final transient ComponentRepresentation<C> representation;
    private final transient List<ReportField> fields;

    public DynamicTableModel(ComponentRepresentation<C> representation) {
        this.representation = representation;
        this.fields = representation.getFields();
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public int getRowCount() {
        return size();
    }

    @Override
    public int getColumnCount() {
        return fields.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Object value = null;
        if(rowIndex != -1) {
            C component = list.get(rowIndex);
            ReportField field = fields.get(columnIndex);
            value = representation.getValueAt(component, field);
        }
        return value;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return fields.get(columnIndex).getType();
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public void addAll(Collection<C> components) {
        int firstRow = list.size();
        list.addAll(components);
        fireTableRowsInserted(firstRow, list.size());
    }

    @Override
    public void add(C component) {
        addAll(Collections.singletonList(component));
    }

    @Override
    public void add(int index, C component) {
        list.add(index, component);
        fireTableRowsInserted(index, index);
    }

    @Override
    public void set(int index, C component) {
        list.set(index, component);
        fireTableRowsUpdated(index, index);
    }

    @Override
    public C get(int row) {
        C component = null;
        if(row >= 0) {
            component = list.get(row);
        }
        return component;
    }

    @Override
    public List<C> get(int[] indices) {
        return Arrays.stream(indices)
                .mapToObj(this::get)
                .toList();
    }

    @Override
    public List<C> getAll() {
        return Collections.unmodifiableList(list);
    }

    protected List<C> getList() {
        return list;
    }

    @Override
    public int indexOf(C component) {
        return list.indexOf(component);
    }

    @Override
    public IntStream indexOf(Collection<C> components) {
        return components.stream()
                .mapToInt(list::indexOf);
    }

    @Override
    public void remove(Collection<C> components) {
        IntStream intStream = indexOf(components);
        IntSummaryStatistics statistics = intStream.summaryStatistics();
        list.removeAll(components);
        fireTableRowsDeleted(statistics.getMin(), statistics.getMax());
    }

    @Override
    public void clear() {
        list.clear();
    }
}
