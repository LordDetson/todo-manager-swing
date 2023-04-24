package by.babanin.todo.view.preference;

import by.babanin.todo.preferences.Preference;
import by.babanin.todo.view.component.table.adjustment.TableColumnAdjustment;
import lombok.Data;

@Data
public class TableColumnAdjustmentPreference implements Preference {

    private TableColumnAdjustment tableColumnAdjustment;
}
