package by.babanin.todo.view.preference;

import java.util.ArrayList;
import java.util.List;

import by.babanin.todo.preferences.Preference;
import lombok.Data;

@Data
public class StringsPreference implements Preference {

    private final List<String> values = new ArrayList<>();
}
