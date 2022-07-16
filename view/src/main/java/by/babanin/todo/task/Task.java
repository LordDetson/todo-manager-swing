package by.babanin.todo.task;

public interface Task<R> extends Runnable {

    String getName();

    void setName(String name);

    void addFinishListener(FinishListener<R> listener);

    void removeFinishListener(FinishListener<R> listener);

    void removeAllFinishListener();
}
