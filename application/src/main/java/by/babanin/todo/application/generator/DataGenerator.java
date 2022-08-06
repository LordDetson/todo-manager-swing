package by.babanin.todo.application.generator;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import by.babanin.todo.application.service.PriorityService;
import by.babanin.todo.application.service.TodoService;
import by.babanin.todo.model.Priority;

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
            Priority high = priorityService.create("High");
            todoService.create("First todo", "Description", high, LocalDate.now());
        }
    }
}
