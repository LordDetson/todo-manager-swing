package by.babanin.todo.view.component.form;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.event.ChangeListener;

import by.babanin.todo.representation.ComponentRepresentation;
import by.babanin.todo.representation.ReportField;
import by.babanin.todo.view.component.CrudStyle;
import by.babanin.todo.view.component.statusbar.LogStatusBarItem;
import by.babanin.todo.view.component.statusbar.StatusBar;
import by.babanin.todo.view.translat.TranslateCode;
import by.babanin.todo.view.translat.Translator;

public class ComponentForm<C> extends JPanel {

    private final ComponentRepresentation<C> componentRepresentation;
    private final FormRowFactory formRowFactory;
    private final CrudStyle crudStyle;
    private final C component;
    private final List<FormRow<?>> formRows = new ArrayList<>();
    private final List<ApplyListener> applyListeners = new ArrayList<>();
    private final Map<ReportField, Object> values = new HashMap<>();
    private Window owner;
    private LogStatusBarItem statusBarItem;
    private JButton applyButton;
    private JButton cancelButton;

    public ComponentForm(Class<C> componentClass, FormRowFactory formRowFactory, CrudStyle crudStyle) {
        this(componentClass, formRowFactory, crudStyle, null);
    }

    public ComponentForm(Class<C> componentClass, FormRowFactory formRowFactory, CrudStyle crudStyle, C component) {
        this.componentRepresentation = ComponentRepresentation.get(componentClass);
        this.formRowFactory = formRowFactory;
        this.crudStyle = crudStyle;
        this.component = component;

        createUiComponents();
        addListeners();
        placeComponents();
    }

    protected void createUiComponents() {
        formRows.addAll(createFormRows());
        statusBarItem = new LogStatusBarItem();
        applyButton = new JButton(Translator.toLocale(TranslateCode.APPLY_BUTTON_TEXT));
        applyButton.setEnabled(false);
        cancelButton = new JButton(Translator.toLocale(TranslateCode.CANCEL_BUTTON_TEXT));
    }

    private List<? extends FormRow<?>> createFormRows() {
        List<String> excludedFields = component == null ?
                crudStyle.getExcludedFieldFromCreationForm() :
                crudStyle.getExcludedFieldFromEditForm();

        return componentRepresentation.getFields().stream()
                .filter(field -> !excludedFields.contains(field.getName()))
                .map(this::createFormRow)
                .toList();
    }

    private FormRow<?> createFormRow(ReportField field) {
        FormRow<Object> formRow = formRowFactory.factor(field);
        if(component != null) {
            formRow.setValue(componentRepresentation.getValueAt(component, field));
        }
        return formRow;
    }

    protected void addListeners() {
        formRows.forEach(formRow -> {
            formRow.addListener(createChangeListener());
            crudStyle.getValidatorFactory()
                    .ifPresent(validatorFactory -> formRow.addValidators(validatorFactory.factor(formRow.getField())));
            statusBarItem.addLogger(formRow.getInputComponent(), formRow.getField().getCaption(), formRow.getLogger());
        });

        applyButton.addActionListener(event -> {
            collectValues(values);
            applyListeners.forEach(applyListener -> applyListener.accept(values));
            hideWindow();
        });
        cancelButton.addActionListener(event -> hideWindow());
    }

    private ChangeListener createChangeListener() {
        return event -> {
            boolean enable = false;
            boolean valid = formRows.stream()
                    .allMatch(formRow -> formRow.getLogger().isValid());
            if(valid) {
                enable = formRows.stream()
                        .filter(formRow -> formRow.getField().isMandatory())
                        .allMatch(formRow -> formRow.getValue() != null) &&
                        formRows.stream()
                                .allMatch(formRow -> formRow.getLogger().isValid());
            }
            applyButton.setEnabled(enable);
        };
    }

    private void collectValues(Map<ReportField, Object> values) {
        formRows.forEach(formRow -> values.put(formRow.getField(), formRow.getValue()));
    }

    private void hideWindow() {
        owner.setVisible(false);
    }

    protected void placeComponents() {
        BorderLayout borderLayout = new BorderLayout();
        setLayout(borderLayout);

        JPanel formRowsPanel = new JPanel();
        formRowsPanel.setLayout(new GridBagLayout());
        for(int i = 0; i < formRows.size(); i++) {
            FormRow<?> formRow = formRows.get(i);
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.gridx = 0;
            constraints.gridy = i;
            constraints.fill = GridBagConstraints.NONE;
            constraints.anchor = GridBagConstraints.LINE_START;
            formRowsPanel.add(formRow.getLabel(), constraints);
            constraints.gridx = 1;
            constraints.gridy = i;
            constraints.fill = GridBagConstraints.BOTH;
            constraints.insets = new Insets(2, 8, 2, 0);
            formRowsPanel.add(formRow.wrap(formRow.getInputComponent()), constraints);
        }

        StatusBar statusBar = new StatusBar();
        statusBar.add(statusBarItem);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(8, 10, 10, 10));
        buttonPanel.add(Box.createHorizontalGlue());
        buttonPanel.add(applyButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPanel.add(cancelButton);

        JPanel formWithStatusBarPanel = new JPanel(new BorderLayout());
        formWithStatusBarPanel.setBorder(BorderFactory.createEmptyBorder(8, 10, 0, 10));
        formWithStatusBarPanel.add(formRowsPanel, BorderLayout.CENTER);
        formWithStatusBarPanel.add(statusBar, BorderLayout.PAGE_END);
        add(formWithStatusBarPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.PAGE_END);
    }

    public void setOwner(Window owner) {
        this.owner = owner;
    }

    public Optional<C> getComponent() {
        return Optional.ofNullable(component);
    }

    public void addApplyListener(ApplyListener listener) {
        applyListeners.add(listener);
    }

    public void removeApplyListener(ApplyListener listener) {
        applyListeners.remove(listener);
    }
}
