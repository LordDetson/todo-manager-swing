package by.babanin.todo.application.generator;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import by.babanin.todo.application.service.PriorityService;
import by.babanin.todo.application.service.TodoService;
import by.babanin.todo.model.Priority;
import by.babanin.todo.model.Status;
import by.babanin.todo.model.Todo;

@Component
public class DataGenerator implements ApplicationListener<ContextRefreshedEvent> {

    private final PriorityService priorityService;
    private final TodoService todoService;

    @Value("${application.data.generation.enable}")
    private boolean dataGenerationEnable;

    public DataGenerator(PriorityService priorityService, TodoService todoService) {
        this.priorityService = priorityService;
        this.todoService = todoService;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if(dataGenerationEnable) {
            todoService.deleteAll();
            priorityService.deleteAll();
            Priority high = priorityService.save(Priority.builder()
                    .name("High")
                    .weight(0)
                    .build());
            todoService.save(Todo.builder()
                    .title("First todo")
                    .description("Description")
                    .priority(high)
                    .status(Status.OPEN)
                    .creationDate(LocalDate.of(2022, 5, 15))
                    .plannedDate(LocalDate.of(2022, 5, 15))
                    .completionDate(LocalDate.of(2022, 5, 15))
                    .build());
        }
    }
}
