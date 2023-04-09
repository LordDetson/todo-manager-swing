package by.babanin.todo.view.component.form;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SpringLayout;
import javax.swing.event.ChangeListener;

import by.babanin.todo.representation.ComponentRepresentation;
import by.babanin.todo.representation.ReportField;
import by.babanin.todo.view.component.CrudStyle;
import by.babanin.todo.view.component.statusbar.LogStatusBarItem;
import by.babanin.todo.view.component.statusbar.StatusBar;
import by.babanin.todo.view.translat.TranslateCode;
import by.babanin.todo.view.translat.Translator;
import by.babanin.todo.view.util.GUIUtils;
import by.babanin.todo.view.util.SpringUtilities;

public class ComponentForm<C> extends JPanel {

    private static final String DIALOG_CLOSING_ACTION_KEY = "closeDialog";
    private final transient ComponentRepresentation<C> componentRepresentation;
    private final transient FormRowFactory formRowFactory;
    private final CrudStyle crudStyle;
    private final transient C component;
    private final List<FormRow<?>> formRows = new ArrayList<>();
    private final List<ApplyListener> applyListeners = new ArrayList<>();
    private final Map<ReportField, Object> values = new HashMap<>();
    private JDialog owner;
    private LogStatusBarItem statusBarItem;
    private transient Action closeDialogAction;
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
        applyButton = new JButton(createApplyAction());
        closeDialogAction = createCloseDialogAction();
        cancelButton = new JButton(closeDialogAction);
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

    private Action createApplyAction() {
        Action action = new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                collectValues(values);
                applyListeners.forEach(applyListener -> applyListener.accept(values));
                closeDialog();
            }
        };
        action.putValue(Action.NAME, Translator.toLocale(TranslateCode.APPLY_BUTTON_TEXT));
        action.setEnabled(false);
        return action;
    }

    private Action createCloseDialogAction() {
        Action action = new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                closeDialog();
            }
        };
        action.putValue(Action.NAME, Translator.toLocale(TranslateCode.CANCEL_BUTTON_TEXT));
        return action;
    }

    private FormRow<?> createFormRow(ReportField field) {
        FormRow<Object> formRow = formRowFactory.factor(field);
        if(component != null) {
            formRow.setComponent(component);
        }
        return formRow;
    }

    protected void addListeners() {
        formRows.forEach(formRow -> {
            formRow.addListener(createChangeListener());
            crudStyle.getValidatorFactory()
                    .ifPresent(validatorFactory -> formRow.addValidators(validatorFactory.factor(formRow.getField())));
            String fieldCaption = Translator.getFieldCaption(formRow.getField());
            statusBarItem.addLogger(formRow.getInputComponent(), fieldCaption, formRow.getLogger());
        });
    }

    private ChangeListener createChangeListener() {
        return event -> {
            boolean enable = false;
            boolean valid = formRows.stream()
                    .allMatch(formRow -> formRow.getLogger().isValid());
            if(valid) {
                enable = formRows.stream()
                        .filter(formRow -> formRow.getField().isMandatory())
                        .allMatch(formRow -> formRow.getNewValue() != null) &&
                        formRows.stream()
                                .allMatch(formRow -> formRow.getLogger().isValid());
            }
            applyButton.setEnabled(enable);
        };
    }

    private void collectValues(Map<ReportField, Object> values) {
        formRows.forEach(formRow -> values.put(formRow.getField(), formRow.getNewValue()));
    }

    private void closeDialog() {
        owner.dispose();
    }

    protected void placeComponents() {
        BorderLayout borderLayout = new BorderLayout();
        setLayout(borderLayout);

        JPanel formRowsPanel = new JPanel();
        formRowsPanel.setLayout(new SpringLayout());
        for(FormRow<?> formRow : formRows) {
            JLabel label = formRow.getLabel();
            JComponent inputComponent = formRow.getInputComponent();
            label.setLabelFor(inputComponent);
            formRowsPanel.add(label);
            formRowsPanel.add(inputComponent);
        }
        SpringUtilities.makeCompactGrid(formRowsPanel,
                formRows.size(), 2, //rows, cols
                8, 4,        //initX, initY
                4, 4);       //xPad, yPad

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
        formWithStatusBarPanel.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 6));
        formWithStatusBarPanel.add(formRowsPanel, BorderLayout.CENTER);
        formWithStatusBarPanel.add(statusBar, BorderLayout.PAGE_END);
        add(formWithStatusBarPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.PAGE_END);
    }

    public void setOwner(JDialog owner) {
        this.owner = owner;
        owner.getRootPane().setDefaultButton(applyButton);
        KeyStroke escapeStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        GUIUtils.addDialogKeyAction(owner, escapeStroke, DIALOG_CLOSING_ACTION_KEY, closeDialogAction);
        statusBarItem.addLogShowingAction(owner);
    }

    public Optional<C> getComponent() {
        return Optional.ofNullable(component);
    }

    public ComponentRepresentation<C> getComponentRepresentation() {
        return componentRepresentation;
    }

    public void addApplyListener(ApplyListener listener) {
        applyListeners.add(listener);
    }

    public void removeApplyListener(ApplyListener listener) {
        applyListeners.remove(listener);
    }
}
