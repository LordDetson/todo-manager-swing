package by.babanin.todo.application.holder;

import java.time.LocalDate;
import java.util.List;

import by.babanin.todo.model.Status;
import by.babanin.todo.model.Todo;

public class TodosHolder extends AbstractTestEntitiesHolder<Todo, Long> {

    public TodosHolder() {
        super(Long::compareTo, Long::sum);
        initEntities();
        initFakeEntities();
    }

    private void initEntities() {
        setEntities(List.of(
                Todo.builder()
                        .title("ToDo1")
                        .description("Description")
                        .status(Status.OPEN)
                        .creationDate(LocalDate.now())
                        .plannedDate(LocalDate.now().plusDays(1))
                        .position(0)
                        .build(),
                Todo.builder()
                        .title("ToDo2")
                        .description("Description")
                        .status(Status.OPEN)
                        .creationDate(LocalDate.now())
                        .plannedDate(LocalDate.now().plusDays(1))
                        .position(1)
                        .build(),
                Todo.builder()
                        .title("ToDo3")
                        .description("Description")
                        .status(Status.OPEN)
                        .creationDate(LocalDate.now())
                        .plannedDate(LocalDate.now().plusDays(1))
                        .position(2)
                        .build(),
                Todo.builder()
                        .title("ToDo4")
                        .description("Description")
                        .status(Status.OPEN)
                        .creationDate(LocalDate.now())
                        .plannedDate(LocalDate.now().plusDays(1))
                        .position(3)
                        .build(),
                Todo.builder()
                        .title("ToDo5")
                        .description("Description")
                        .status(Status.OPEN)
                        .creationDate(LocalDate.now())
                        .plannedDate(LocalDate.now().plusDays(1))
                        .position(4)
                        .build()
        ));
    }

    private void initFakeEntities() {
        setFakeEntities(List.of(
                Todo.builder()
                        .title("FakeToDo1")
                        .description("Description")
                        .status(Status.OPEN)
                        .creationDate(LocalDate.now())
                        .plannedDate(LocalDate.now().plusDays(1))
                        .position(10)
                        .build(),
                Todo.builder()
                        .title("FakeToDo1")
                        .description("Description")
                        .status(Status.OPEN)
                        .creationDate(LocalDate.now())
                        .plannedDate(LocalDate.now().plusDays(1))
                        .position(11)
                        .build()
        ));
    }
}
