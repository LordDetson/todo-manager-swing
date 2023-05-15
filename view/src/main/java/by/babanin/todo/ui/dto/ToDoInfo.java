package by.babanin.todo.ui.dto;

import java.time.LocalDate;
import java.util.Objects;

import by.babanin.ext.representation.Indexable;
import by.babanin.ext.representation.ReportableComponent;
import by.babanin.ext.representation.ReportableField;
import by.babanin.todo.model.Status;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import lombok.experimental.FieldNameConstants;

@ReportableComponent(name = "todo")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
@FieldNameConstants
public class ToDoInfo implements Indexable {

    Long id;

    @ReportableField(index = 0, mandatory = true)
    String title;

    @ReportableField(index = 1)
    String description;

    @ReportableField(index = 2)
    PriorityInfo priority;

    @ReportableField(index = 3, mandatory = true)
    Status status;

    @ReportableField(index = 4, mandatory = true)
    LocalDate creationDate;

    @ReportableField(index = 5, mandatory = true)
    LocalDate plannedDate;

    @ReportableField(index = 6)
    LocalDate completionDate;

    long position;

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(o == null || getClass() != o.getClass()) {
            return false;
        }
        ToDoInfo todo = (ToDoInfo) o;
        return Objects.equals(id, todo.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
