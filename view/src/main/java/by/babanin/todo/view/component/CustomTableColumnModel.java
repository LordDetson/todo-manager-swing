package by.babanin.todo.view.component;

import java.util.List;

import javax.swing.table.DefaultTableColumnModel;

import by.babanin.todo.representation.ReportField;
import by.babanin.todo.view.util.AppUtils;

public class CustomTableColumnModel extends DefaultTableColumnModel {

    public CustomTableColumnModel(List<ReportField> fields) {
        int modelIndex = 0;
        for(ReportField field : fields) {
            addColumn(AppUtils.createTableColumn(field, modelIndex++));
        }
    }
}
