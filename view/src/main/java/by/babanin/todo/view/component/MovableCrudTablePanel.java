package by.babanin.todo.view.component;

import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import by.babanin.todo.application.service.IndexableCrudService;
import by.babanin.todo.model.Indexable;
import by.babanin.todo.model.Persistent;
import by.babanin.todo.task.SwapTask;
import by.babanin.todo.view.component.crud.Crud;

public abstract class MovableCrudTablePanel
        <C extends Persistent<I> & Indexable,
                I,
                S extends IndexableCrudService<C, I>,
                T extends JTable,
                M extends IndexableTableModel<C>>
        extends CrudTablePanel<C, I, S, T, M> {

    private transient Action moveUpAction;
    private transient Action moveDownAction;

    protected MovableCrudTablePanel(Crud<C, I, S> crud) {
        super(crud);
    }

    @Override
    public void createUiComponents() {
        super.createUiComponents();
        CrudStyle crudStyle = getCrud().getCrudStyle();
        moveUpAction = new RunnableAction(
                crudStyle.getMoveUpButtonIcon(),
                crudStyle.getMoveUpButtonToolTip(),
                KeyEvent.VK_UP,
                this::moveUp
        );
        moveDownAction = new RunnableAction(
                crudStyle.getMoveDownButtonIcon(),
                crudStyle.getMoveDownButtonToolTip(),
                KeyEvent.VK_DOWN,
                this::moveDown
        );
    }

    @Override
    public void addListeners() {
        super.addListeners();
        JTable table = getTable();
        ListSelectionModel selectionModel = table.getSelectionModel();
        selectionModel.addListSelectionListener(e -> {
            int selectionCount = selectionModel.getSelectedItemsCount();
            moveUpAction.setEnabled(selectionCount == 1 && table.getSelectedRow() != 0);
            moveDownAction.setEnabled(selectionCount == 1 && table.getSelectedRow() != table.getRowCount() - 1);
        });
    }

    private void moveUp() {
        moveComponent(Direction.UP);
    }

    private void moveDown() {
        moveComponent(Direction.DOWN);
    }

    private void moveComponent(Direction direction) {
        IndexableTableModel<C> model = getModel();
        C selectedComponent = getSelectedComponent();
        int selectedIndex = model.indexOf(selectedComponent);
        int directionCount = direction == Direction.UP ? -1 : 1;
        int nextIndex = selectedIndex + directionCount;
        SwapTask<C, I, S> task = new SwapTask<>(getCrud().getCrudService(), selectedIndex, nextIndex);
        task.addFinishListener(unused -> {
            model.swap(selectedIndex, nextIndex);
            selectRow(nextIndex);
        });
        task.execute();
    }

    public Action getMoveUpAction() {
        return moveUpAction;
    }

    public Action getMoveDownAction() {
        return moveDownAction;
    }
}
