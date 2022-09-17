package by.babanin.todo.task;

import by.babanin.todo.task.listener.ExceptionListener;
import by.babanin.todo.task.listener.FinishListener;
import by.babanin.todo.task.listener.StartListener;

public interface Task<R> {

    void execute();

    String getName();

    void setName(String name);

    void addStartListener(StartListener listener);

    void removeStartListener(StartListener listener);

    void addFinishListener(by.babanin.todo.task.listener.FinishListener<R> listener);

    void removeFinishListener(FinishListener<R> listener);

    void addExceptionListener(by.babanin.todo.task.listener.ExceptionListener listener);

    void removeExceptionListener(ExceptionListener listener);
}
