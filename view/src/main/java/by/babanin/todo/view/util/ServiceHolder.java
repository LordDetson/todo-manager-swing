package by.babanin.todo.view.util;

import by.babanin.todo.application.PriorityService;
import by.babanin.todo.application.TodoService;
import by.babanin.todo.view.exception.ViewException;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ServiceHolder {

    private PriorityService priorityService;
    private TodoService todoService;

    public static PriorityService getPriorityService() {
        return priorityService;
    }

    public static void setPriorityService(PriorityService priorityService) {
        if(ServiceHolder.priorityService != null) {
            throw new ViewException("Priority service already exist");
        }
        ServiceHolder.priorityService = priorityService;
    }

    public static TodoService getTodoService() {
        return todoService;
    }

    public static void setTodoService(TodoService todoService) {
        if(ServiceHolder.todoService != null) {
            throw new ViewException("Todo service already exist");
        }
        ServiceHolder.todoService = todoService;
    }
}
