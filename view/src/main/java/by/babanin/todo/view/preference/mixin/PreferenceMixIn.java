package by.babanin.todo.view.preference.mixin;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import by.babanin.todo.preferences.PreferencesGroup;
import by.babanin.todo.view.preference.DimensionPreference;
import by.babanin.todo.view.preference.PointPreference;
import by.babanin.todo.view.preference.SplitPanePreference;
import by.babanin.todo.view.preference.TableColumnPreference;
import by.babanin.todo.view.preference.TableColumnsPreference;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type")
@JsonSubTypes({
        @Type(value = PreferencesGroup.class, name = "group"),
        @Type(value = DimensionPreference.class, name = "dimension"),
        @Type(value = PointPreference.class, name = "point"),
        @Type(value = SplitPanePreference.class, name = "splitPane"),
        @Type(value = TableColumnsPreference.class, name = "tableColumns"),
        @Type(value = TableColumnPreference.class, name = "tableColumn")
})
public interface PreferenceMixIn {

}