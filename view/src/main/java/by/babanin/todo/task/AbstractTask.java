package by.babanin.todo.task;

import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.List;

import by.babanin.todo.application.exception.ApplicationException;

public abstract class AbstractTask<R> implements Task<R> {

    private final List<FinishListener<R>> finishListeners = new ArrayList<>();
    private String name;

    public abstract R execute();

    @Override
    public void run() {
        try {
            R result = execute();
            EventQueue.invokeLater(() -> finishListeners.forEach(listener -> listener.accept(result)));
        }
        catch(Exception throwable) {
            throw new ApplicationException(throwable);
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void addFinishListener(FinishListener<R> listener) {
        finishListeners.add(listener);
    }

    @Override
    public void removeFinishListener(FinishListener<R> listener) {
        finishListeners.remove(listener);
    }

    @Override
    public void removeAllFinishListener() {
        finishListeners.clear();
    }
}
