package by.babanin.todo.view.preference;

import java.awt.Point;

import com.fasterxml.jackson.annotation.JsonProperty;

import by.babanin.todo.preferences.Preference;
import lombok.Data;

@Data
public class PointPreference implements Preference {

    @JsonProperty("point")
    private Point point;
}
