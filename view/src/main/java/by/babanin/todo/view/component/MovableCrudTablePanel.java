package by.babanin.todo.view.component;

import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.Action;
import javax.swing.JTable;

import by.babanin.todo.application.service.AbstractIndexableCrudService;
import by.babanin.todo.model.Indexable;
import by.babanin.todo.model.Persistent;
import by.babanin.todo.representation.ComponentRepresentation;
import by.babanin.todo.representation.ReportField;
import by.babanin.todo.task.SwapTask;
import by.babanin.todo.view.component.form.FormRowFactory;
import by.babanin.todo.view.exception.ViewException;
import by.babanin.todo.view.util.ServiceHolder;

public abstract class MovableCrudTablePanel<C extends Persistent<I> & Indexable, I> extends CrudTablePanel<C, I> {

    private transient Action moveUpAction;
    private transient Action moveDownAction;

    protected MovableCrudTablePanel(Class<C> componentClass, FormRowFactory formRowFactory, CrudStyle crudStyle) {
        super(componentClass, formRowFactory, crudStyle);
    }

    @Override
    public void createUiComponents() {
        super.createUiComponents();
        CrudStyle crudStyle = getCrudStyle();
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
    protected IndexableTableModel<C> createTableModel(ComponentRepresentation<C> representation, List<ReportField> fields) {
        return new IndexableTableModel<>(representation, fields);
    }

    @Override
    protected void actionEnabling() {
        super.actionEnabling();
        JTable table = getTable();
        int selectionCount = table.getSelectionModel().getSelectedItemsCount();
        moveUpAction.setEnabled(selectionCount == 1 && table.getSelectedRow() != 0);
        moveDownAction.setEnabled(selectionCount == 1 && table.getSelectedRow() != table.getRowCount() - 1);
    }

    @Override
    public IndexableTableModel<C> getModel() {
        return (IndexableTableModel<C>) super.getModel();
    }

    private void moveUp() {
        moveComponent(Direction.UP);
    }

    private void moveDown() {
        moveComponent(Direction.DOWN);
    }

    private void moveComponent(Direction direction) {
        IndexableTableModel<C> model = getModel();
        C selectedComponent = getSelectedComponent()
                .orElseThrow(() -> new ViewException("Can't move component because component is not selected"));
        int selectedIndex = model.indexOf(selectedComponent);
        int directionCount = direction == Direction.UP ? -1 : 1;
        int nextIndex = selectedIndex + directionCount;
        Class<C> componentClass = getRepresentation().getComponentClass();
        AbstractIndexableCrudService<C, I> service = (AbstractIndexableCrudService<C, I>) ServiceHolder.getCrudService(componentClass);
        SwapTask<C, I, AbstractIndexableCrudService<C, I>> task = new SwapTask<>(service, selectedIndex, nextIndex);
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
