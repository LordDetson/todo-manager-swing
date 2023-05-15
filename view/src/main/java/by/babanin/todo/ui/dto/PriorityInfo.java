package by.babanin.todo.ui.dto;

import java.util.Objects;

import by.babanin.ext.representation.Indexable;
import by.babanin.ext.representation.ReportableComponent;
import by.babanin.ext.representation.ReportableField;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import lombok.experimental.FieldNameConstants;

@ReportableComponent(name = "priority")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
@FieldNameConstants
public class PriorityInfo implements Indexable {

    Long id;

    @ReportableField(index = 0, mandatory = true)
    String name;

    long position;

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(o == null || getClass() != o.getClass()) {
            return false;
        }
        PriorityInfo priority = (PriorityInfo) o;
        return name.equals(priority.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
