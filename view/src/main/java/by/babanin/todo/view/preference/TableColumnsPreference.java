package by.babanin.todo.view.preference;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import com.fasterxml.jackson.annotation.JsonProperty;

import by.babanin.todo.preferences.Preference;
import by.babanin.todo.preferences.PreferenceException;
import lombok.Getter;

@Getter
public class TableColumnsPreference implements Preference, Iterable<TableColumnPreference> {

    @JsonProperty("tableColumns")
    private final List<TableColumnPreference> tableColumnPreferences = new ArrayList<>();

    public void add(TableColumnModel tableColumnModel) {
        if(tableColumnModel == null) {
            throw new PreferenceException("TableColumnModel can not be null");
        }
        else if(tableColumnModel.getColumnCount() == 0) {
            throw new PreferenceException("TableColumnModel can not be empty");
        }
        Enumeration<TableColumn> columns = tableColumnModel.getColumns();
        int i = 0;
        while(columns.hasMoreElements()) {
            tableColumnPreferences.add(new TableColumnPreference(columns.nextElement(), i++));
        }
    }

    @Override
    public Iterator<TableColumnPreference> iterator() {
        return tableColumnPreferences.iterator();
    }

    public void apply(TableColumnModel tableColumnModel) {
        for(TableColumnPreference preference : this) {
            preference.findTableColumn(tableColumnModel).ifPresent(tableColumn -> {
                int currentPosition = tableColumnModel.getColumnIndex(preference.getIdentifier());
                int newPosition = preference.getPosition();
                if(currentPosition != newPosition) {
                    tableColumnModel.moveColumn(currentPosition, newPosition);
                }
                int currentWidth = tableColumn.getWidth();
                int newWidth = preference.getWidth();
                if(currentWidth != newWidth) {
                    tableColumn.setWidth(newWidth);
                    tableColumn.setPreferredWidth(newWidth);
                }
            });
        }
    }
}
