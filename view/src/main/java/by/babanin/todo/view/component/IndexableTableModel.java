package by.babanin.todo.view.component;

import java.util.Collections;
import java.util.List;

import by.babanin.todo.model.Indexable;
import by.babanin.todo.representation.ComponentRepresentation;
import by.babanin.todo.representation.ReportField;
import by.babanin.todo.view.exception.ViewException;

public class IndexableTableModel<C extends Indexable> extends TableModel<C> {

    public IndexableTableModel(ComponentRepresentation<C> representation, List<ReportField> fields) {
        super(representation, fields);
    }

    public void swap(int index1, int index2) {
        if(index1 < 0 || index2 < 0) {
            throw new ViewException("Indices shouldn't be less 0");
        }
        Collections.swap(getList(), index1, index2);
        fireTableRowsUpdated(Integer.min(index1, index2), Integer.max(index1, index2));
    }
}