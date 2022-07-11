package by.babanin.todo.model;

import java.time.LocalDate;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

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

@Entity(name = "todos")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
@FieldNameConstants
public class Todo implements Persistent<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @Column(nullable = false, length = 16)
    @ReportField(index = 0, mandatory = true)
    @NonNull
    String title;

    @Column(nullable = false)
    @ReportField(index = 1)
    String description;

    @ManyToOne
    @JoinColumn(name="priority_id")
    @ReportField(index = 2, mandatory = true)
    Priority priority;

    @Column(nullable = false)
    @ReportField(index = 3, mandatory = true)
    @NonNull
    Status status;

    @Column(nullable = false)
    @ReportField(index = 4, mandatory = true)
    @NonNull
    LocalDate creationDate;

    @Column(nullable = false)
    @ReportField(index = 5, mandatory = true)
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
