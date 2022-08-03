package by.babanin.todo.view.component.logger;

import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import by.babanin.todo.view.translat.TranslateCode;
import by.babanin.todo.view.translat.Translator;

public class CombinedLogPanel extends JTabbedPane {
    private final transient CombinedLogger combinedLogger;

    public CombinedLogPanel(CombinedLogger combinedLogger) {
        this.combinedLogger = combinedLogger;
        combinedLogger.getMessageTypes().forEach(this::addTab);
    }

    private void addTab(LogMessageType type) {
        List<String> messages = combinedLogger.getLoggers().stream()
                .flatMap(logger -> logger.getMessages(type).stream())
                .toList();
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn(Translator.toLocale(TranslateCode.MESSAGES), messages.toArray());

        int count = combinedLogger.getMessageCount(type);
        addTab(type.getCaption() + " (" + count + ")", type.getIcon(), new JScrollPane(new JTable(model)));
    }
}
