package by.babanin.todo.model;

import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

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
import lombok.experimental.FieldNameConstants;

@Entity(name = "priorities")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString(exclude = "todos")
@FieldDefaults(level = AccessLevel.PRIVATE)
@FieldNameConstants
public class Priority implements Persistent<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @Column(unique = true, nullable = false, length = 16)
    @ReportableField(index = 0, mandatory = true)
    @NonNull
    String name;

    @Column(unique = true, nullable = false)
    @ReportableField(index = 1, mandatory = true)
    @NonNull
    long weight;

    @OneToMany(mappedBy = "priority")
    Set<Todo> todos;

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
