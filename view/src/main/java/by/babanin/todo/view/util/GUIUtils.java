package by.babanin.todo.view.util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.beans.PropertyChangeEvent;
import java.util.Objects;

import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import com.github.lgooddatepicker.components.DatePickerSettings.DateArea;

import by.babanin.todo.view.exception.ResourceException;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class GUIUtils {

    private static final int LARGE_FRAME_SCALE = 85;
    private static final int HALF_FRAME_SCALE = 50;
    private static final int SMALL_FRAME_SCALE = 35;
    private static final double SCALE_BASE = 100;

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
}
