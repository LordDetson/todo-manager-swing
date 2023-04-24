package by.babanin.todo.view.preference;

import by.babanin.todo.preferences.Preference;
import lombok.Data;

@Data
public class StringPreference implements Preference {

    private String value;
}
