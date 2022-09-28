package by.babanin.todo.view;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JToolBar;

import by.babanin.todo.model.Priority;
import by.babanin.todo.task.listener.FinishListener;
import by.babanin.todo.view.component.View;

public class PriorityPanel extends JPanel implements View {

    private JToolBar toolBar;
    private PriorityCrudTablePanel crudTablePanel;

    public PriorityPanel() {
        super(new BorderLayout());
        initialize();
    }

    @Override
    public void createUiComponents() {
        crudTablePanel = new PriorityCrudTablePanel();

        toolBar = new JToolBar();
        toolBar.add(crudTablePanel.getShowCreationDialogAction());
        toolBar.add(crudTablePanel.getShowEditDialogAction());
        toolBar.add(crudTablePanel.getShowDeleteConfirmDialogAction());
        toolBar.add(crudTablePanel.getMoveUpAction());
        toolBar.add(crudTablePanel.getMoveDownAction());
    }

    @Override
    public void addListeners() {
        // do nothing
    }

    @Override
    public void placeComponents() {
        add(toolBar, BorderLayout.PAGE_START);
        add(crudTablePanel, BorderLayout.CENTER);
    }

    @Override
    public void load() {
        crudTablePanel.load();
    }

    @Override
    public void clear() {
        crudTablePanel.clear();
    }

    public void addEditListener(FinishListener<Priority> listener) {
        crudTablePanel.addEditListener(listener);
    }

    public void addDeletionListener(FinishListener<List<Priority>> listener) {
        crudTablePanel.addDeletionListener(listener);
    }
}
