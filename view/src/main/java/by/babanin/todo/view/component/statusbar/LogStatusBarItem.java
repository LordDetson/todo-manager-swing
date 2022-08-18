package by.babanin.todo.view.component.statusbar;

import java.awt.Component;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
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
import javax.swing.JRootPane;
import javax.swing.KeyStroke;

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

    private static final String LOG_SHOWING_ACTION_KEY = "showLog";
    private static final String LOG_CLOSING_ACTION_KEY = "closeLog";
    private final Map<LogOwner, Logger> loggerMap = new LinkedHashMap<>();
    private final transient CombinedLogger combinedLogger = new CombinedLogger();

    private final Action showLogAction;

    public LogStatusBarItem() {
        showLogAction = createShowLogAction();
        getLabel().addMouseListener(createClickActionListener());
    }

    private AbstractAction createShowLogAction() {
        return new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent event) {
                Window window = GUIUtils.getWindowOwner((Component) event.getSource());
                JDialog dialog = new JDialog(window, ModalityType.APPLICATION_MODAL);
                dialog.setContentPane(new CombinedLogPanel(combinedLogger));
                dialog.setTitle(Translator.toLocale(TranslateCode.LOG_MESSAGES));
                Dimension smallFrameSize = GUIUtils.getSmallFrameSize();
                dialog.setMinimumSize(smallFrameSize);
                dialog.setSize(smallFrameSize);
                dialog.setLocationRelativeTo(window);
                addCloseLogAction(dialog);
                dialog.setVisible(true);
            }

            private void addCloseLogAction(JDialog dialog) {
                KeyStroke escapeStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
                GUIUtils.addDialogKeyAction(dialog, escapeStroke, LOG_CLOSING_ACTION_KEY, new AbstractAction() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        dialog.dispose();
                    }
                });
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
                reload();
            }

            @Override
            public void logCleared() {
                reload();
            }
        };
    }

    private void reload() {
        if(!loggerMap.isEmpty()) {
            clear();
            load();
        }
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

    public void addLogShowingAction(JDialog dialog) {
        JRootPane rootPane = dialog.getRootPane();
        KeyStroke keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_L, InputEvent.ALT_DOWN_MASK);
        rootPane.getInputMap(WHEN_IN_FOCUSED_WINDOW).put(keyStroke, LOG_SHOWING_ACTION_KEY);
        rootPane.getActionMap().put(LOG_SHOWING_ACTION_KEY, showLogAction);
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
