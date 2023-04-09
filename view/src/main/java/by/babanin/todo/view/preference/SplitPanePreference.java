package by.babanin.todo.view.preference;

import javax.swing.JSplitPane;

import com.fasterxml.jackson.annotation.JsonProperty;

import by.babanin.todo.preferences.Preference;
import lombok.Data;

@Data
public class SplitPanePreference implements Preference {

    @JsonProperty("proportion")
    private double proportion;

    public void storeProportion(JSplitPane splitPane) {
        setProportion(roundToSecondDecimalPlace(splitPane.getDividerLocation() / (double) splitPane.getHeight()));
    }

    private static double roundToSecondDecimalPlace(double number) {
        return Math.round(number * 100) / 100.0;
    }
}
