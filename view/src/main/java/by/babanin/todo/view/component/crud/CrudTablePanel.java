package by.babanin.todo.view.component.crud;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;

import by.babanin.todo.model.Persistent;
import by.babanin.todo.view.component.tablemodel.TableModel;
import by.babanin.todo.view.component.tablemodel.TableModelLoader;

public class CrudTablePanel<E extends Persistent<I>, I> extends JPanel{

    public static final CrudStyle DEFAULT_STYLE = new CrudStyle();

    private final Class<E> componentClass;
    private final TableModelLoader<E, I> tableModelLoader;
    private final CrudStyle crudStyle;

    private TableModel<E> model;
    private JToolBar toolBar;
    private JButton createButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton moveUpButton;
    private JButton moveDownButton;
    private JTable table;

    protected CrudTablePanel(Class<E> componentClass, TableModelLoader<E, I> tableModelLoader) {
        this(componentClass, tableModelLoader, DEFAULT_STYLE);
    }

    protected CrudTablePanel(Class<E> componentClass, TableModelLoader<E, I> tableModelLoader, CrudStyle crudStyle) {
        this.componentClass = componentClass;
        this.tableModelLoader = tableModelLoader;
        this.crudStyle = crudStyle;

        createComponents();
        setupTable(table, model);
        addListeners();
        placeComponents();
    }

    protected void createComponents() {
        toolBar = new JToolBar();
        model = new TableModel<>(componentClass);
        table = new JTable();

        createButton = new JButton(crudStyle.getCreateButtonIcon());
        editButton = new JButton(crudStyle.getEditButtonIcon());
        deleteButton = new JButton(crudStyle.getDeleteButtonIcon());
        moveUpButton = new JButton(crudStyle.getMoveUpButtonIcon());
        moveDownButton = new JButton(crudStyle.getMoveDownButtonIcon());
    }

    protected void setupTable(JTable table, TableModel<E> model) {
        table.setModel(model);
    }

    protected void addListeners() {
        //TODO implement
    }

    protected void placeComponents() {
        addToolBarComponent(createButton);
        addToolBarComponent(editButton);
        addToolBarComponent(deleteButton);
        addToolBarComponent(moveUpButton);
        addToolBarComponent(moveDownButton);

        setLayout(new BorderLayout());
        add(toolBar, BorderLayout.PAGE_START);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    public void load() {
        tableModelLoader.load(model);
    }

    protected void addToolBarComponent(Component component) {
        toolBar.add(component);
    }
}
