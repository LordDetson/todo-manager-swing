package by.babanin.todo.application.holder;

import java.util.List;

import by.babanin.todo.model.Priority;

public class PrioritiesHolder extends AbstractTestEntitiesHolder<Priority, Long> {

    public PrioritiesHolder() {
        super(Long::compareTo, Long::sum);
        initEntities();
        initFakeEntities();
    }

    private void initEntities() {
        setEntities(List.of(
                Priority.builder()
                        .name("FirstPriority")
                        .position(0)
                        .build(),
                Priority.builder()
                        .name("SecondPriority")
                        .position(1)
                        .build(),
                Priority.builder()
                        .name("ThirdPriority")
                        .position(2)
                        .build(),
                Priority.builder()
                        .name("ForthPriority")
                        .position(3)
                        .build(),
                Priority.builder()
                        .name("FifthPriority")
                        .position(4)
                        .build()
        ));
    }

    private void initFakeEntities() {
        setFakeEntities(List.of(
                Priority.builder()
                        .name("FirstFPriority")
                        .position(10)
                        .build(),
                Priority.builder()
                        .name("SecondFPriority")
                        .position(11)
                        .build()
        ));
    }
}
