package by.babanin.todo.model;

import java.time.LocalDate;
import java.util.Objects;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Todo implements Entity<Long> {

    Long id;

    @ReportField(index = 0)
    @NonNull
    String title;

    @ReportField(index = 1)
    String description;

    @ReportField(index = 2)
    Priority priority;

    @ReportField(index = 3)
    @NonNull
    Status status;

    @ReportField(index = 4)
    @NonNull
    LocalDate creationDate;

    @ReportField(index = 5)
    @NonNull
    LocalDate plannedDate;

    @ReportField(index = 6)
    LocalDate completionDate;

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(o == null || getClass() != o.getClass()) {
            return false;
        }
        Todo todo = (Todo) o;
        return title.equals(todo.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title);
    }
}
