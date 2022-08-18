package by.babanin.todo.view.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Objects;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPopupMenu;
import javax.swing.JRootPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableColumn;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import com.github.lgooddatepicker.components.DatePickerSettings.DateArea;

import by.babanin.todo.representation.ReportField;
import by.babanin.todo.view.exception.ResourceException;
import by.babanin.todo.view.exception.ViewException;
import by.babanin.todo.view.translat.Translator;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class GUIUtils {

    private static final int LARGE_FRAME_SCALE = 85;
    private static final int HALF_FRAME_SCALE = 50;
    private static final int SMALL_FRAME_SCALE = 35;
    private static final double SCALE_BASE = 100;

    private static JFrame mainWindow;
    private static KeyCash vks;

    public static Dimension getFullScreenSize() {
        return Toolkit.getDefaultToolkit().getScreenSize();
    }

    public static Dimension getLargeFrameSize() {
        return getScaledMainFrameSize(LARGE_FRAME_SCALE);
    }

    public static Dimension getHalfFrameSize() {
        return getScaledMainFrameSize(HALF_FRAME_SCALE);
    }

    public static Dimension getSmallFrameSize() {
        return getScaledMainFrameSize(SMALL_FRAME_SCALE);
    }

    public static Dimension getScaledMainFrameSize(int scale) {
        return getScaledMainFrameSize(scale, scale);
    }

    public static Dimension getScaledMainFrameSize(int widthScale, int heightScale) {
        Dimension fullScreenSize = getFullScreenSize();
        return scaleDimension(fullScreenSize, widthScale, heightScale);
    }

    private static Dimension scaleDimension(Dimension dimension, int widthScale, int heightScale) {
        dimension.width = (int) (dimension.width * widthScale / SCALE_BASE);
        dimension.height = (int) (dimension.height * heightScale / SCALE_BASE);
        return dimension;
    }

    public static String buildResourcePath(String path, String name, String extension) {
        StringBuilder resourcePathBuilder = new StringBuilder();
        if(path != null && !path.isBlank()) {
            resourcePathBuilder.append(path);
        }
        if(name != null && !name.isBlank()) {
            resourcePathBuilder.append(name);
        }
        else {
            throw new ResourceException("name can't be empty");
        }
        if(extension != null && !extension.isBlank()) {
            resourcePathBuilder.append(".").append(extension);
        }
        return resourcePathBuilder.toString();
    }

    public static void addChangeListener(JTextComponent text, ChangeListener changeListener) {
        Objects.requireNonNull(text);
        Objects.requireNonNull(changeListener);
        DocumentListener dl = new DocumentListener() {

            private int lastChange = 0;
            private int lastNotifiedChange = 0;

            @Override
            public void insertUpdate(DocumentEvent e) {
                changedUpdate(e);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                changedUpdate(e);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                lastChange++;
                SwingUtilities.invokeLater(() -> {
                    if(lastNotifiedChange != lastChange) {
                        lastNotifiedChange = lastChange;
                        changeListener.stateChanged(new ChangeEvent(text));
                    }
                });
            }
        };
        text.addPropertyChangeListener("document", (PropertyChangeEvent e) -> {
            Document d1 = (Document) e.getOldValue();
            Document d2 = (Document) e.getNewValue();
            if(d1 != null) {
                d1.removeDocumentListener(dl);
            }
            if(d2 != null) {
                d2.addDocumentListener(dl);
            }
            dl.changedUpdate(null);
        });
        Document d = text.getDocument();
        if(d != null) {
            d.addDocumentListener(dl);
        }
    }

    public static DatePicker createDatePicker() {
        UIDefaults defaults = UIManager.getDefaults();

        Color panelBackground = defaults.getColor("Panel.background");
        Color buttonBackground = defaults.getColor("Button.background");
        Color buttonForeground = defaults.getColor("Button.foreground");
        Color textFieldBackground = defaults.getColor("TextField.background");
        Color textFieldForeground = defaults.getColor("TextField.foreground");

        DatePickerSettings dateSettings = new DatePickerSettings();

        dateSettings.setColor(DateArea.BackgroundOverallCalendarPanel, panelBackground);
        dateSettings.setColor(DateArea.CalendarBackgroundNormalDates, panelBackground);
        dateSettings.setColor(DateArea.CalendarBorderSelectedDate, panelBackground);
        dateSettings.setColor(DateArea.BackgroundMonthAndYearMenuLabels, panelBackground);
        dateSettings.setColor(DateArea.BackgroundTodayLabel, panelBackground);
        dateSettings.setColor(DateArea.BackgroundClearLabel, panelBackground);

        dateSettings.setColor(DateArea.BackgroundCalendarPanelLabelsOnHover, buttonBackground);
        dateSettings.setColor(DateArea.CalendarBackgroundSelectedDate, buttonBackground);
        dateSettings.setColorBackgroundWeekdayLabels(buttonBackground, true);
        dateSettings.setColorBackgroundWeekNumberLabels(buttonBackground, true);

        dateSettings.setColor(DateArea.CalendarDefaultTextHighlightedDates, buttonForeground);
        dateSettings.setColor(DateArea.CalendarTextNormalDates, buttonForeground);
        dateSettings.setColor(DateArea.CalendarTextWeekdays, buttonForeground);
        dateSettings.setColor(DateArea.CalendarTextWeekNumbers, buttonForeground);

        dateSettings.setColor(DateArea.TextFieldBackgroundInvalidDate, textFieldBackground);
        dateSettings.setColor(DateArea.TextFieldBackgroundValidDate, textFieldBackground);
        dateSettings.setColor(DateArea.TextFieldBackgroundVetoedDate, textFieldBackground);

        dateSettings.setColor(DateArea.DatePickerTextValidDate, textFieldForeground);
        dateSettings.setColor(DateArea.DatePickerTextVetoedDate, textFieldForeground);

        dateSettings.setAllowKeyboardEditing(false);
        dateSettings.setBorderCalendarPopup(new JTextField().getBorder());

        DatePicker datePicker = new DatePicker(dateSettings);
        datePicker.getComponentDateTextField().setBorder(new JTextField().getBorder());
        return datePicker;
    }

    public static TableColumn createTableColumn(ReportField field, int modelIndex) {
        TableColumn column = new TableColumn(modelIndex);
        column.setIdentifier(field.getName());
        column.setHeaderValue(Translator.getFieldCaption(field));
        return column;
    }

    public static String stacktraceToString(Throwable e) {
        if(e == null) {
            return "";
        }

        StringWriter writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        e.printStackTrace(printWriter);
        printWriter.flush();

        return writer.toString();
    }

    public static void removeFocus(JComponent component) {
        component.setRequestFocusEnabled(false);
        component.setFocusable(false);
    }

    public static Window getWindowOwner(Component component) {
        // can no longer assert component has window ancestor, since multi-user messages
        // can auto-close dialogs or remove component from hierarchy (hierarchical table child table collapse)

        Window window;
        if(component == null || component instanceof Window) {
            window = (Window) component;
        }
        else {
            window = getWindowAncestor(component);
        }

        while(window != null && !window.isShowing()) {
            window = window.getOwner();
        }
        if(window != null) {
            return window;
        }
        return getActiveWindow();
    }

    private static Window getWindowAncestor(Component component) {
        for(Container parent = component.getParent(); parent != null; parent = parent.getParent()) {
            if(parent instanceof Window window) {
                return window;
            }
            else if(parent instanceof JPopupMenu popupMenu && parent.getParent() == null) {
                return getWindowAncestor(popupMenu.getInvoker());
            }
        }
        return null;
    }

    public static Window getActiveWindow() {
        Window window = KeyboardFocusManager.getCurrentKeyboardFocusManager().getActiveWindow();
        while(window != null && (!(window instanceof Dialog) && !(window instanceof Frame))) {
            window = window.getOwner();
        }

        if(window != null) {
            return window;
        }
        return getMainWindow();
    }

    public static void setMainWindow(JFrame window) {
        mainWindow = window;
    }

    public static JFrame getMainWindow() {
        return mainWindow;
    }

    public static String getMnemonicKeyDescription(int mnemonicKey) {
        return "(Alt+" + GUIUtils.getVKText(mnemonicKey) + ")";
    }

    public static String getVKText(int keyCode) {
        KeyCash vkCollect = getKeyCash();
        Integer key = keyCode;
        String name;
        int expectedModifiers = (Modifier.PUBLIC | Modifier.STATIC | Modifier.FINAL);

        Field[] fields = KeyEvent.class.getDeclaredFields();
        for(Field field : fields) {
            try {
                if(field.getModifiers() == expectedModifiers
                        && field.getType() == Integer.TYPE
                        && field.getName().startsWith("VK_")
                        && field.getInt(KeyEvent.class) == keyCode) {
                    name = field.getName();
                    vkCollect.put(name, key);
                    return name.substring(3);
                }
            }
            catch(IllegalAccessException e) {
                assert (false);
            }
        }
        throw new ViewException("Unknown key code");
    }

    private static KeyCash getKeyCash() {
        if(vks == null) {
            vks = new KeyCash();
        }
        return vks;
    }

    public static void addDialogKeyAction(JDialog dialog, KeyStroke keyStroke, String actionKey, Action action) {
        JRootPane rootPane = dialog.getRootPane();
        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyStroke, actionKey);
        rootPane.getActionMap().put(actionKey, action);
    }
}
