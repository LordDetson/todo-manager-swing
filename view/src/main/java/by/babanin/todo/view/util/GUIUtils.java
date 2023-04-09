package by.babanin.todo.view.util;

import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableColumn;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

import com.formdev.flatlaf.util.SystemInfo;

import by.babanin.todo.preferences.PreferenceAware;
import by.babanin.todo.preferences.PreferencesSupport;
import by.babanin.todo.representation.ReportField;
import by.babanin.todo.view.exception.ViewException;
import by.babanin.todo.view.translat.TranslateCode;
import by.babanin.todo.view.translat.Translator;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class GUIUtils {

    private static final int LARGE_FRAME_SCALE = 85;
    private static final int HALF_FRAME_SCALE = 50;
    private static final int SMALL_FRAME_SCALE = 35;
    private static final double SCALE_BASE = 100;

    private static final String MNEMONIC_MODIFIER_FOR_MAC = "OPT";
    private static final String MNEMONIC_MODIFIER_FOR_OTHER_OS = "ALT";
    private static final String MNEMONIC_KEY_DESCRIPTION_FORMAT = "(%s+%s)";

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

    public static void addChangeListener(JTextComponent text, ChangeListener changeListener) {
        Objects.requireNonNull(text);
        Objects.requireNonNull(changeListener);
        DocumentListener dl = new DocumentListener() {

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
                changeListener.stateChanged(new ChangeEvent(text));
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
        String mnemonicModifier = MNEMONIC_MODIFIER_FOR_OTHER_OS;
        if(SystemInfo.isMacOS) {
            mnemonicModifier = MNEMONIC_MODIFIER_FOR_MAC;
        }
        return String.format(MNEMONIC_KEY_DESCRIPTION_FORMAT, mnemonicModifier, GUIUtils.getVKText(mnemonicKey));
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

    public static JLabel createHyperlinkLabel(String hyperlink) {
        return createHyperlinkLabel(hyperlink, hyperlink);
    }

    public static JLabel createHyperlinkLabel(String hyperlink, String text) {
        JLabel hyperlinkLabel = new JLabel("<html><a href=\"#\">" + text + "</a></html>");
        hyperlinkLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        hyperlinkLabel.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent event) {
                try {
                    Desktop.getDesktop().browse(new URI(hyperlink));
                }
                catch(IOException | URISyntaxException e) {
                    throw new ViewException(e);
                }
            }
        });
        return hyperlinkLabel;
    }

    public static void addCloseActionOnEscape(JDialog dialog, String key) {
        KeyStroke escapeStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        GUIUtils.addDialogKeyAction(dialog, escapeStroke, key, new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });
    }

    public static <T extends Window & PreferenceAware<?>> void addPreferenceSupport(T preferenceAware) {
        PreferencesSupport preferencesSupport = new PreferencesSupport();
        preferencesSupport.put(preferenceAware);
        preferenceAware.addWindowListener(new WindowAdapter() {

            @Override
            public void windowOpened(WindowEvent e) {
                preferencesSupport.apply();
            }

            @Override
            public void windowClosing(WindowEvent e) {
                preferencesSupport.save();
            }

            @Override
            public void windowClosed(WindowEvent e) {
                preferencesSupport.save();
            }
        });
        JRootPane rootPane = SwingUtilities.getRootPane(preferenceAware);
        addResetPreferencesPopupMenuItem(rootPane, preferenceAware);
    }

    private static void addResetPreferencesPopupMenuItem(JComponent component, PreferenceAware<?> preferenceAware) {
        JPopupMenu popupMenu = component.getComponentPopupMenu();
        if(popupMenu == null) {
            popupMenu = new JPopupMenu();
            component.setComponentPopupMenu(popupMenu);

        }
        popupMenu.add(new AbstractAction(Translator.toLocale(TranslateCode.RESET_PREFERENCES).formatted()) {

            @Override
            public void actionPerformed(ActionEvent e) {
                preferenceAware.resetToDefault();
            }
        });
        getAllComponentsRecursively(component).forEach(child -> {
            if(child instanceof JComponent jComponent) {
                jComponent.setInheritsPopupMenu(true);
            }
        });
    }

    public static List<Component> getAllComponentsRecursively(Container container) {
        List<Component> result = new ArrayList<>();
        collectComponentsRecursively(container, result);
        return result;
    }

    private static void collectComponentsRecursively(Component component, Collection<Component> result) {
        result.add(component);
        if(component instanceof Container container) {
            for(Component child : container.getComponents()) {
                collectComponentsRecursively(child, result);
            }
        }
    }
}
