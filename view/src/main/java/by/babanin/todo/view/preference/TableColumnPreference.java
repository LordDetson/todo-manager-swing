package by.babanin.todo.view.preference;

import java.util.Optional;

import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import com.fasterxml.jackson.annotation.JsonProperty;

import by.babanin.todo.preferences.Preference;
import lombok.Data;

@Data
public class TableColumnPreference implements Preference {

    @JsonProperty("identifier")
    private String identifier;

    @JsonProperty("position")
    private int position;

    @JsonProperty("width")
    private int width;

    public TableColumnPreference() {
        // for deserialization
    }

    public TableColumnPreference(TableColumn tableColumn, int position) {
        this.identifier = tableColumn.getIdentifier().toString();
        this.width = tableColumn.getWidth();
        this.position = position;
    }

    public Optional<TableColumn> findTableColumn(TableColumnModel tableColumnModel) {
        int columnIndex = tableColumnModel.getColumnIndex(identifier);
        if(columnIndex >= 0) {
            return Optional.of(tableColumnModel.getColumn(columnIndex));
        }
        return Optional.empty();
    }
}
