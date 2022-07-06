package by.babanin.todo.model;

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
public class Priority implements Entity<Long> {

    Long id;

    @ReportField(index = 0)
    @NonNull
    String name;

    @ReportField(index = 1)
    @NonNull
    int weight;

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(o == null || getClass() != o.getClass()) {
            return false;
        }
        Priority priority = (Priority) o;
        return weight == priority.weight && name.equals(priority.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, weight);
    }
}
