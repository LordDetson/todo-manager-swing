package by.babanin.todo.view.preference;

import java.awt.Dimension;

import com.fasterxml.jackson.annotation.JsonProperty;

import by.babanin.todo.preferences.Preference;
import lombok.Data;

@Data
public class DimensionPreference implements Preference {

    @JsonProperty("dimension")
    private Dimension dimension;
}
