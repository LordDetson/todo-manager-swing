package by.babanin.todo.view.priority;

import java.awt.BorderLayout;

import javax.swing.JTable;
import javax.swing.JToolBar;

import by.babanin.todo.application.service.PriorityService;
import by.babanin.todo.model.Priority;
import by.babanin.todo.representation.ComponentRepresentation;
import by.babanin.todo.view.component.IndexableTableModel;
import by.babanin.todo.view.component.MovableCrudTablePanel;
import by.babanin.todo.view.component.crud.Crud;

public class PriorityPanel extends MovableCrudTablePanel<Priority, Long, PriorityService, JTable, IndexableTableModel<Priority>> {

    private JToolBar toolBar;

    public PriorityPanel(PriorityCrud crud) {
        super(crud);
    }

    @Override
    public void createUiComponents() {
        super.createUiComponents();

        toolBar = new JToolBar();
        Crud<Priority, Long, PriorityService> crud = getCrud();
        toolBar.add(crud.getShowCreationDialogAction());
        toolBar.add(crud.getShowUpdateDialogAction());
        toolBar.add(crud.getShowDeletionConfirmationDialogAction());
        toolBar.add(getMoveUpAction());
        toolBar.add(getMoveDownAction());
    }

    @Override
    protected IndexableTableModel<Priority> createTableModel(ComponentRepresentation<Priority> representation) {
        return new IndexableTableModel<>(representation);
    }

    @Override
    protected JTable createTable(IndexableTableModel<Priority> model) {
        return new JTable(model);
    }

    @Override
    public void placeComponents() {
        add(toolBar, BorderLayout.PAGE_START);
        super.placeComponents();
    }
}
