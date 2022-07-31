package by.babanin.todo.view.component.statusbar;

import java.awt.Component;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.StringJoiner;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JDialog;

import by.babanin.todo.view.component.logger.CombinedLogPanel;
import by.babanin.todo.view.component.logger.CombinedLogger;
import by.babanin.todo.view.component.logger.LogMessageType;
import by.babanin.todo.view.component.logger.LogUpdateListener;
import by.babanin.todo.view.component.logger.Logger;
import by.babanin.todo.view.exception.ViewException;
import by.babanin.todo.view.translat.TranslateCode;
import by.babanin.todo.view.translat.Translator;
import by.babanin.todo.view.util.GUIUtils;

public class LogStatusBarItem extends StatusBarItem {

    private final Map<LogOwner, Logger> loggerMap = new LinkedHashMap<>();
    private final CombinedLogger combinedLogger = new CombinedLogger();

    private final AbstractAction showLogAction;

    public LogStatusBarItem() {
        showLogAction = createShowLogAction();
        getLabel().addMouseListener(createClickActionListener());
    }

    private AbstractAction createShowLogAction() {
        return new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent event) {
                Window window = GUIUtils.getWindowOwner((Component) event.getSource());
                JDialog dialog = new JDialog(window);
                dialog.setContentPane(new CombinedLogPanel(combinedLogger));
                dialog.setTitle(Translator.toLocale(TranslateCode.LOG_MESSAGES));
                dialog.setSize(GUIUtils.getSmallFrameSize());
                dialog.setLocationRelativeTo(window);
                dialog.setVisible(true);
            }
        };
    }

    private MouseListener createClickActionListener() {
        return new MouseAdapter() {

            @Override
            public void mouseReleased(MouseEvent event) {
                if(!event.isConsumed() && event.getClickCount() == 1 && event.getButton() == MouseEvent.BUTTON1
                        && showLogAction.isEnabled()) {
                    showLogAction.actionPerformed(new ActionEvent(LogStatusBarItem.this, ActionEvent.ACTION_PERFORMED,
                            (String) showLogAction.getValue(Action.ACTION_COMMAND_KEY)));
                    event.consume();
                }
            }
        };
    }

    public void addLogger(Component owner, String title, Logger log) {
        LogOwner logOwner = new LogOwner(owner, title);
        if(loggerMap.containsKey(logOwner)) {
            throw new ViewException("A log supplier is already registered for given owner with same title: " + title);
        }
        log.addLogChangeListener(createStatusBarItemUpdater());
        loggerMap.put(logOwner, log);
        updateStatusBarInfo();
    }

    private LogUpdateListener createStatusBarItemUpdater() {
        return new LogUpdateListener() {

            @Override
            public void logChanged() {
                if(!loggerMap.isEmpty()) {
                    clear();
                    load();
                }
            }

            @Override
            public void logCleared() {
                clear();
            }
        };
    }

    private void load() {
        for(Entry<LogOwner, Logger> entry : loggerMap.entrySet()) {
            LogOwner key = entry.getKey();
            Logger logger = entry.getValue();
            for(LogMessageType type : LogMessageType.values()) {
                if(logger.hasMessages(type)) {
                    logger.getMessages(type).forEach(message -> combinedLogger.logMessage(key.title(), type, message));
                }
            }
        }
        updateStatusBarInfo();
    }

    private void clear() {
        combinedLogger.clear();
        updateStatusBarInfo();
    }

    public void updateStatusBarInfo() {
        int count = combinedLogger.getMessageCount();
        if(count == 0) {
            setIcon(null);
            setText("");
            setToolTipText("");
            showLogAction.setEnabled(false);
        }
        else {
            Collection<Logger> loggers = combinedLogger.getLoggers();
            Logger firstLogger = loggers.iterator().next();
            LogMessageType firstType = firstLogger.getMessageTypes().iterator().next();
            String message = firstLogger.getMessages(firstType).iterator().next();
            StringJoiner joiner = new StringJoiner("\n");
            loggers.forEach(logger -> logger.getMessageTypes().forEach(type -> logger.getMessages(type).forEach(joiner::add)));

            setIcon(firstType.getIcon());
            setToolTipText(joiner.toString());
            if(count == 1) {
                setText(message);
            }
            else {
                setText(Translator.toLocale(TranslateCode.ERROR_AND_X_MORE_CLICK).formatted(message, count));
            }
            showLogAction.setEnabled(true);
        }
    }

    private record LogOwner(Component component, String title) {

        @Override
        public boolean equals(Object o) {
            if(this == o) {
                return true;
            }
            if(o == null || getClass() != o.getClass()) {
                return false;
            }
            LogOwner that = (LogOwner) o;
            return Objects.equals(component, that.component) && Objects.equals(title, that.title);
        }

        @Override
        public int hashCode() {
            return Objects.hash(component, title);
        }
    }
}
