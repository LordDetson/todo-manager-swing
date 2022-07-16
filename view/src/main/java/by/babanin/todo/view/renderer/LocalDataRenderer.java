package by.babanin.todo.view.renderer;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import javax.swing.table.DefaultTableCellRenderer;

public class LocalDataRenderer extends DefaultTableCellRenderer {

    private final transient DateTimeFormatter dateFormatter;

    public LocalDataRenderer() {
        this(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG));
    }

    public LocalDataRenderer(DateTimeFormatter dateFormatter) {
        this.dateFormatter = dateFormatter;
    }

    @Override
    protected void setValue(Object value) {
        if(value instanceof LocalDate localDate) {
            String formatted = localDate.format(dateFormatter);
            setText(formatted);
        }
        else {
            super.setValue(value);
        }
    }
}
