package by.babanin.todo.view.settings;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import by.babanin.todo.preferences.PreferenceAware;
import by.babanin.todo.preferences.PreferencesGroup;
import by.babanin.todo.view.component.CardPanel;
import by.babanin.todo.view.config.SettingsUpdateEvent;
import by.babanin.todo.view.preference.SplitPanePreference;
import by.babanin.todo.view.preference.StringPreference;
import by.babanin.todo.view.renderer.SettingViewTypeRenderer;
import by.babanin.todo.view.translat.TranslateCode;
import by.babanin.todo.view.translat.Translator;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SettingsPanel extends JPanel implements PreferenceAware<PreferencesGroup> {

    private static final String SPLIT_PANE_KEY = "splitPane";
    private static final String SELECTED_VIEW_KEY = "selectedView";
    private static final String MESSAGE_VIEW_ID = "messageView";
    private static final Double DEFAULT_SPLIT_PANE_PROPORTION = 0.3;

    private final ApplicationEventPublisher publisher;
    private final Settings settings;

    private JList<SettingViewType> list;
    private CardPanel<SettingView<Setting>> cardPanel;
    private JSplitPane splitPane;
    private JButton okButton;
    private Action cancelAction;
    private JButton cancelButton;
    private Action applyAction;

    public SettingsPanel(ApplicationEventPublisher publisher, Settings settings) {
        super(new BorderLayout());
        this.publisher = publisher;
        this.settings = settings;
        setName("settingsPanel");

        createUiComponents();
        addListeners();
        placeComponents();
    }

    private void createUiComponents() {
        list = new JList<>(SettingViewType.values());
        list.setCellRenderer(new SettingViewTypeRenderer());
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        cardPanel = new CardPanel<>();
        cardPanel.put(MESSAGE_VIEW_ID, MessagePanel::new);
        SettingChangeListener<Setting> enableApplyActionListener = createEnableApplyActionListener();
        Arrays.stream(SettingViewType.values())
                .forEach(type -> cardPanel.put(type.getId(), () -> type.createView(settings))
                        .addListener(settingView -> settingView.addListener(enableApplyActionListener)));

        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setLeftComponent(new JScrollPane(list));
        splitPane.setRightComponent(cardPanel);

        Action okAction = createOkAction();
        applyAction = createApplyAction();
        applyAction.setEnabled(false);
        okButton = new JButton(okAction);
        cancelButton = new JButton();
    }

    private SettingChangeListener<Setting> createEnableApplyActionListener() {
        return setting -> applyAction.setEnabled(
                cardPanel.getAllInitialized().stream()
                        .anyMatch(SettingView::shouldApply));
    }

    private AbstractAction createOkAction() {
        return new AbstractAction(Translator.toLocale(TranslateCode.SETTINGS_OK_BUTTON_TEXT)) {

            @Override
            public void actionPerformed(ActionEvent e) {
                applyAction.actionPerformed(e);
                cancelAction.actionPerformed(e);
            }
        };
    }

    private AbstractAction createApplyAction() {
        return new AbstractAction(Translator.toLocale(TranslateCode.SETTINGS_APPLY_BUTTON_TEXT)) {

            @Override
            public void actionPerformed(ActionEvent e) {
                cardPanel.getAllInitialized().stream()
                        .filter(SettingView::shouldApply)
                        .forEach(SettingsPanel.this::applyAndPublishEvent);
            }
        };
    }

    private void addListeners() {
        list.addListSelectionListener(e -> cardPanel.showCard(getSelectedViewId()));
    }

    private void placeComponents() {
        add(splitPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(8, 10, 10, 10));
        buttonPanel.add(Box.createHorizontalGlue());
        buttonPanel.add(okButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPanel.add(cancelButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPanel.add(new JButton(applyAction));
        add(buttonPanel, BorderLayout.PAGE_END);
    }

    private String getSelectedViewId() {
        SettingViewType selectedView = list.getSelectedValue();
        if(selectedView == null) {
            return MESSAGE_VIEW_ID;
        }
        return selectedView.getId();
    }

    private void selectView(String id) {
        if(id.equals(MESSAGE_VIEW_ID)) {
            list.setSelectedValue(null, false);
            cardPanel.showCard(MESSAGE_VIEW_ID);
        }
        else {
            SettingViewType settingViewType = SettingViewType.get(id);
            list.setSelectedValue(settingViewType, true);
        }
    }

    private void applyAndPublishEvent(SettingView<?> settingsView) {
        applyAction.setEnabled(false);
        EventQueue.invokeLater(() -> publisher.publishEvent(new SettingsUpdateEvent(settingsView.apply())));
    }

    public void setCancelAction(Action action) {
        action.putValue(Action.NAME, Translator.toLocale(TranslateCode.SETTINGS_CANCEL_BUTTON_TEXT));
        this.cancelAction = action;
        cancelButton.setAction(cancelAction);
    }

    public JButton getDefaultButton() {
        return okButton;
    }

    @Override
    public void apply(PreferencesGroup preferencesGroup) {
        EventQueue.invokeLater(() -> {
            preferencesGroup.get(SPLIT_PANE_KEY)
                    .ifPresent(preference -> splitPane.setDividerLocation(((SplitPanePreference) preference).getProportion()));
            preferencesGroup.get(SELECTED_VIEW_KEY)
                    .ifPresent(preference -> selectView(((StringPreference) preference).getValue()));
        });
        list.requestFocus();
    }

    @Override
    public PreferencesGroup createCurrentPreference() {
        SplitPanePreference splitPanePreference = new SplitPanePreference();
        splitPanePreference.storeProportion(splitPane);
        StringPreference selectedViewPreference = new StringPreference();
        selectedViewPreference.setValue(getSelectedViewId());
        PreferencesGroup preferencesGroup = new PreferencesGroup();
        preferencesGroup.put(SPLIT_PANE_KEY, splitPanePreference);
        preferencesGroup.put(SELECTED_VIEW_KEY, selectedViewPreference);
        return preferencesGroup;
    }

    @Override
    public PreferencesGroup createDefaultPreference() {
        SplitPanePreference splitPanePreference = new SplitPanePreference();
        splitPanePreference.setProportion(DEFAULT_SPLIT_PANE_PROPORTION);
        StringPreference selectedViewPreference = new StringPreference();
        selectedViewPreference.setValue(MESSAGE_VIEW_ID);
        PreferencesGroup preferencesGroup = new PreferencesGroup();
        preferencesGroup.put(SPLIT_PANE_KEY, splitPanePreference);
        preferencesGroup.put(SELECTED_VIEW_KEY, selectedViewPreference);
        return preferencesGroup;
    }

    @Override
    public String getKey() {
        return getName();
    }

    private static final class MessagePanel extends JPanel implements SettingView<Setting> {

        public MessagePanel() {
            super(new GridBagLayout());
            add(new JLabel(Translator.toLocale(TranslateCode.SETTINGS_DESELECTED_MESSAGE)));
        }

        @Override
        public Setting getOriginal() {
            return null;
        }

        @Override
        public Setting getAccumulator() {
            return null;
        }

        @Override
        public SettingViewType getType() {
            return null;
        }

        @Override
        public void addListener(SettingChangeListener<Setting> listener) {
            // do nothing since it's placeholder
        }

        @Override
        public List<SettingChangeListener<Setting>> getListeners() {
            return Collections.emptyList();
        }
    }
}



