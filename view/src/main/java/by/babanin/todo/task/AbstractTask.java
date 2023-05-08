package by.babanin.todo.task;

import java.awt.EventQueue;
import java.beans.PropertyChangeListenerProxy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import by.babanin.ext.component.IndeterminateProgressModalDialog;
import by.babanin.ext.component.util.GUIUtils;
import by.babanin.todo.task.exception.TaskException;
import by.babanin.todo.task.listener.ExceptionListener;
import by.babanin.todo.task.listener.FinishListener;
import by.babanin.todo.task.listener.StartListener;

public abstract class AbstractTask<R> extends SwingWorker<R, Void> implements Task<R> {

    private static final String STATE_PROPERTY = "state";

    private final List<StartListener> startListeners = new ArrayList<>();
    private final List<FinishListener<R>> finishListeners = new ArrayList<>();
    private final List<ExceptionListener> exceptionListeners = new ArrayList<>();
    private final IndeterminateProgressModalDialog progressDialog = new IndeterminateProgressModalDialog(GUIUtils.getMainWindow());
    private String name;

    protected AbstractTask() {
        addPropertyChangeListener(new PropertyChangeListenerProxy(STATE_PROPERTY, event -> {
            if(getState() == StateValue.STARTED) {
                startListeners.forEach(StartListener::performed);
            }
        }));
        addStartListener(() -> progressDialog.setVisible(true));
        addFinishListener(result -> progressDialog.dispose());
        addExceptionListener(exception -> progressDialog.dispose());
    }

    protected abstract R body() throws Exception;

    @Override
    protected R doInBackground() throws Exception {
        return body();
    }

    @Override
    protected void done() {
        try {
            R result = get();
            finishListeners.forEach(listener -> listener.accept(result));
        }
        catch(InterruptedException exception) {
            Thread.currentThread().interrupt();
            handleException(exception);
            throw new TaskException(exception);
        }
        catch(ExecutionException exception) {
            Exception cause = (Exception) exception.getCause();
            handleException(cause);
            throw new TaskException(cause);
        }
    }

    private void handleException(Exception exception) {
        if(SwingUtilities.isEventDispatchThread()) {
            notifyException(exception);
        }
        else {
            EventQueue.invokeLater(() -> notifyException(exception));
        }
    }

    private void notifyException(Exception exception) {
        exceptionListeners.forEach(listener -> listener.accept(exception));
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
    public void addStartListener(StartListener listener) {
        startListeners.add(listener);
    }

    @Override
    public void removeStartListener(StartListener listener) {
        startListeners.remove(listener);
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
    public void addExceptionListener(ExceptionListener listener) {
        exceptionListeners.add(listener);
    }

    @Override
    public void removeExceptionListener(ExceptionListener listener) {
        exceptionListeners.remove(listener);
    }
}
