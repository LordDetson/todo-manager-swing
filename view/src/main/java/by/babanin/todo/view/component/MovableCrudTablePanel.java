package by.babanin.todo.view.component;

import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JTable;

import by.babanin.todo.application.service.IndexableCrudService;
import by.babanin.todo.model.Indexable;
import by.babanin.todo.model.Persistent;
import by.babanin.todo.task.SwapTask;
import by.babanin.todo.task.TaskManager;
import by.babanin.todo.view.component.form.FormRowFactory;
import by.babanin.todo.view.exception.ViewException;
import by.babanin.todo.view.util.ServiceHolder;

public abstract class MovableCrudTablePanel<C extends Persistent<I> & Indexable, I> extends CrudTablePanel<C, I> {

    private JButton moveUpButton;
    private JButton moveDownButton;

    protected MovableCrudTablePanel(Class<C> componentClass, FormRowFactory formRowFactory, CrudStyle crudStyle) {
        super(componentClass, formRowFactory, crudStyle);
    }

    @Override
    protected void createUiComponents() {
        super.createUiComponents();
        CrudStyle crudStyle = getCrudStyle();
        moveUpButton = new JButton(crudStyle.getMoveUpButtonIcon());
        moveDownButton = new JButton(crudStyle.getMoveDownButtonIcon());
    }

    @Override
    protected IndexableTableModel<C> createTableModel(Class<C> componentClass) {
        return new IndexableTableModel<>(componentClass);
    }

    @Override
    protected void addListeners() {
        super.addListeners();
        moveUpButton.addActionListener(this::moveUp);
        moveDownButton.addActionListener(this::moveDown);
    }

    @Override
    protected void placeComponents() {
        super.placeComponents();
        addToolBarComponent(moveUpButton);
        addToolBarComponent(moveDownButton);
    }

    @Override
    protected void actionEnabling() {
        super.actionEnabling();
        JTable table = getTable();
        int selectionCount = table.getSelectionModel().getSelectedItemsCount();
        moveUpButton.setEnabled(selectionCount == 1 && table.getSelectedRow() != 0);
        moveDownButton.setEnabled(selectionCount == 1 && table.getSelectedRow() != table.getRowCount() - 1);
    }

    @Override
    public IndexableTableModel<C> getModel() {
        return (IndexableTableModel<C>) super.getModel();
    }

    private void moveUp(ActionEvent event) {
        moveComponent(Direction.UP);
    }

    private void moveDown(ActionEvent event) {
        moveComponent(Direction.DOWN);
    }

    private void moveComponent(Direction direction) {
        IndexableTableModel<C> model = getModel();
        C selectedComponent = getSelectedComponent()
                .orElseThrow(() -> new ViewException("Can't move component because component is not selected"));
        int selectedIndex = model.indexOf(selectedComponent);
        int directionCount = direction == Direction.UP ? -1 : 1;
        int nextIndex = selectedIndex + directionCount;
        IndexableCrudService<C, I> service = (IndexableCrudService<C, I>) ServiceHolder.getCrudService(getComponentClass());
        SwapTask<C, I, IndexableCrudService<C, I>> task = new SwapTask<>(service, selectedIndex, nextIndex);
        task.addFinishListener(unused -> {
            model.swap(selectedIndex, nextIndex);
            selectRow(nextIndex);
        });
        TaskManager.run(task);
    }
}
