package by.babanin.todo.view.util;

import by.babanin.todo.application.service.CrudService;
import by.babanin.todo.application.service.PriorityService;
import by.babanin.todo.application.service.TodoService;
import by.babanin.todo.model.Persistent;
import by.babanin.todo.model.Priority;
import by.babanin.todo.model.Todo;
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

    @SuppressWarnings("unchecked")
    public static <E extends Persistent<I>, I> CrudService<E, I> getCrudService(Class<E> componentClass) {
        if(componentClass.isAssignableFrom(Priority.class)) {
            return (CrudService<E, I>) getPriorityService();
        }
        else if(componentClass.isAssignableFrom(Todo.class)) {
            return (CrudService<E, I>) getTodoService();
        }
        throw new ViewException(componentClass.getSimpleName() + " doesn't support");
    }
}
