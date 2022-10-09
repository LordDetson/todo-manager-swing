package by.babanin.todo.view.component;

import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import by.babanin.todo.application.service.CrudService;
import by.babanin.todo.model.Persistent;
import by.babanin.todo.representation.ComponentRepresentation;
import by.babanin.todo.view.component.crud.Crud;

public abstract class CrudTablePanel
                <C extends Persistent<I>,
                I,
                S extends CrudService<C, I>,
                T extends JTable,
                M extends ListTableModel<C>>
        extends JPanel
        implements View {

    private final List<String> excludedFields = new ArrayList<>();

    private final Crud<C, I, S> crud;

    private boolean initialized;
    private M model;
    private CustomTableColumnModel<C> columnModel;
    private T table;

    protected CrudTablePanel(Crud<C, I, S> crud) {
        super(new BorderLayout());
        this.crud = crud;
        crud.setComponentToUpdateSupplier(this::getSelectedComponent);
        crud.setComponentsToDeleteSupplier(this::getSelectedComponents);
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }

    @Override
    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    @Override
    public void createUiComponents() {
        model = createTableModel(crud.getRepresentation());
        table = createTable(model);
        columnModel = new CustomTableColumnModel<>(crud.getRepresentation());
        table.setColumnModel(columnModel);
    }

    protected abstract M createTableModel(ComponentRepresentation<C> representation);

    protected abstract T createTable(M model);

    @Override
    public void addListeners() {
        table.getSelectionModel().addListSelectionListener(event -> crud.actionEnabling());

        crud.addCreationListener(this::handleCreation);
        crud.addReadListener(this::handleRead);
        crud.addUpdateListener(this::handleUpdate);
        crud.addDeletionListener(this::handleDeletion);
        crud.addExceptionListener(this::handleException);
    }

    @Override
    public void placeComponents() {
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    protected void handleCreation(C result) {
        model.add(result);
        selectComponent(result);
    }

    protected void handleRead(List<C> result) {
        model.addAll(result);
        selectFirstRow();
    }

    protected void handleUpdate(C result) {
        int row = table.getSelectedRow();
        model.set(row, result);
        selectRow(row);
    }

    protected void handleDeletion(List<C> result) {
        model.remove(result);
    }

    protected void handleException(Exception exception) {
        reload();
    }

    @Override
    public void load() {
        crud.read();
    }

    @Override
    public void clear() {
        table.clearSelection();
        model.clear();
    }

    public C getSelectedComponent() {
        return model.get(table.getSelectedRow());
    }

    public List<C> getSelectedComponents() {
        return model.get(table.getSelectionModel().getSelectedIndices());
    }

    public void selectComponent(C component) {
        int row = model.indexOf(component);
        selectRow(row);
    }

    public void selectRow(int row) {
        table.getSelectionModel().setSelectionInterval(row, row);
        table.scrollRectToVisible(new Rectangle(table.getCellRect(row, 0, true)));
    }

    public void selectFirstRow() {
        if(model.size() > 0) {
            selectRow(0);
        }
    }

    public Crud<C, I, S> getCrud() {
        return crud;
    }

    public M getModel() {
        return model;
    }

    public T getTable() {
        return table;
    }

    public void excludeField(String... fields) {
        this.excludedFields.addAll(List.of(fields));
        for(String fieldName : excludedFields) {
            int columnIndex = columnModel.getColumnIndex(fieldName);
            columnModel.removeColumn(columnModel.getColumn(columnIndex));
        }
    }
}
