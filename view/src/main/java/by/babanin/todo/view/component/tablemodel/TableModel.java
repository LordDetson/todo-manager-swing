package by.babanin.todo.view.component.tablemodel;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.apache.commons.lang3.StringUtils;

import by.babanin.todo.model.ReportField;
import by.babanin.todo.view.exception.ViewException;
import by.babanin.todo.view.translat.Translator;

public class TableModel<T> extends AbstractTableModel {

    private final List<T> list = new ArrayList<>();
    private final List<Field> fields = new ArrayList<>();
    private final Class<T> componentClass;

    public TableModel(Class<T> componentClass) {
        this.componentClass = componentClass;
        fields.addAll(List.of(componentClass.getDeclaredFields()));
        fields.removeIf(field -> field.getAnnotation(ReportField.class) == null);
        if(fields.isEmpty()) {
            throw new ViewException("Component doesn't have reportable fields");
        }
        fields.sort(Comparator.comparingInt(field -> field.getAnnotation(ReportField.class).index()));
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
        T component = list.get(rowIndex);
        Field field = fields.get(columnIndex);
        try {
            for (PropertyDescriptor pd : Introspector.getBeanInfo(componentClass).getPropertyDescriptors()) {
                if(pd.getReadMethod() != null && field.getName().equals(pd.getName())) {
                    return pd.getReadMethod().invoke(component);
                }
            }
            throw new ViewException(field.getName() + " field getter isn't exist");
        }
        catch(IntrospectionException | InvocationTargetException | IllegalAccessException e) {
            throw new ViewException(e);
        }
    }

    @Override
    public String getColumnName(int column) {
        Field field = fields.get(column);
        String caption = Translator.getFieldCaption(componentClass, field);
        if(StringUtils.isNotBlank(caption)) {
            return caption;
        }
        return super.getColumnName(column);
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        Field field = fields.get(columnIndex);
        return field.getType();
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public void addAll(Collection<? extends T> collection) {
        int firstRow = list.size();
        list.addAll(collection);
        fireTableRowsInserted(firstRow, list.size());
    }
}
