package by.babanin.todo.view.component.datepicker;

import java.awt.Color;

import javax.swing.JTextField;
import javax.swing.UIDefaults;
import javax.swing.UIManager;

import com.github.lgooddatepicker.components.DatePickerSettings;

public class CustomDatePickerSettings extends DatePickerSettings {

    public CustomDatePickerSettings() {
        super();
        UIDefaults defaults = UIManager.getDefaults();

        Color panelBackground = defaults.getColor("Panel.background");
        Color buttonBackground = defaults.getColor("Button.background");
        Color buttonForeground = defaults.getColor("Button.foreground");
        Color textFieldBackground = defaults.getColor("TextField.background");
        Color textFieldForeground = defaults.getColor("TextField.foreground");

        setColor(DateArea.BackgroundOverallCalendarPanel, panelBackground);
        setColor(DateArea.CalendarBackgroundNormalDates, panelBackground);
        setColor(DateArea.CalendarBorderSelectedDate, panelBackground);
        setColor(DateArea.BackgroundMonthAndYearMenuLabels, panelBackground);
        setColor(DateArea.BackgroundTodayLabel, panelBackground);
        setColor(DateArea.BackgroundClearLabel, panelBackground);
        setColor(DateArea.CalendarBackgroundVetoedDates, panelBackground);

        setColor(DateArea.BackgroundCalendarPanelLabelsOnHover, buttonBackground);
        setColor(DateArea.CalendarBackgroundSelectedDate, buttonBackground);
        setColorBackgroundWeekdayLabels(buttonBackground, true);
        setColorBackgroundWeekNumberLabels(buttonBackground, true);

        setColor(DateArea.CalendarDefaultTextHighlightedDates, buttonForeground);
        setColor(DateArea.CalendarTextNormalDates, buttonForeground);
        setColor(DateArea.CalendarTextWeekdays, buttonForeground);
        setColor(DateArea.CalendarTextWeekNumbers, buttonForeground);

        setColor(DateArea.TextFieldBackgroundInvalidDate, textFieldBackground);
        setColor(DateArea.TextFieldBackgroundValidDate, textFieldBackground);
        setColor(DateArea.TextFieldBackgroundVetoedDate, textFieldBackground);

        setColor(DateArea.DatePickerTextValidDate, textFieldForeground);
        setColor(DateArea.DatePickerTextVetoedDate, textFieldForeground);

        setAllowKeyboardEditing(false);
        setBorderCalendarPopup(new JTextField().getBorder());
    }
}
