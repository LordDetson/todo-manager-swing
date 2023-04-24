package by.babanin.todo.view.settings;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.commons.lang3.StringUtils;

import com.formdev.flatlaf.FlatClientProperties;

import by.babanin.todo.view.component.TextAreaPanel;

public abstract class AbstractSettingView<T extends Setting> extends JPanel
        implements SettingView<T> {

    private final T setting;
    private final T accumulator;
    private final SettingViewType type;
    private final List<SettingChangeListener<T>> listeners = new ArrayList<>();

    @SuppressWarnings("unchecked")
    protected AbstractSettingView(T setting, SettingViewType type) {
        super(new BorderLayout());
        this.setting = setting;
        this.accumulator = (T) setting.clone();
        this.type = type;

        setName(type.getId());
        setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

        JLabel title = new JLabel(type.getTitle());
        title.putClientProperty(FlatClientProperties.STYLE_CLASS, "h2");

        String description = type.getDescription();
        if(StringUtils.isNoneBlank(description)) {
            title.setAlignmentX(LEFT_ALIGNMENT);

            TextAreaPanel descriptionPanel = new TextAreaPanel(false);
            descriptionPanel.setEditable(false);
            descriptionPanel.setBorder(BorderFactory.createEmptyBorder());
            descriptionPanel.setAlignmentX(LEFT_ALIGNMENT);
            descriptionPanel.setText(type.getDescription());

            JPanel titleAndDescriptionPanel = new JPanel();
            BoxLayout boxLayout = new BoxLayout(titleAndDescriptionPanel, BoxLayout.Y_AXIS);
            titleAndDescriptionPanel.setLayout(boxLayout);
            titleAndDescriptionPanel.add(title);
            titleAndDescriptionPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            titleAndDescriptionPanel.add(descriptionPanel);

            add(titleAndDescriptionPanel, BorderLayout.PAGE_START);
        }
        else {
            add(title, BorderLayout.PAGE_START);
        }

        JPanel contentPanel = createContentPanel(setting, accumulator, type);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        add(contentPanel, BorderLayout.CENTER);
    }

    protected abstract JPanel createContentPanel(T setting, T accumulator, SettingViewType settingViewType);

    @Override
    public T getOriginal() {
        return setting;
    }

    @Override
    public T getAccumulator() {
        return accumulator;
    }

    @Override
    public SettingViewType getType() {
        return type;
    }

    @Override
    public void addListener(SettingChangeListener<T> listener) {
        listeners.add(listener);
    }

    @Override
    public List<SettingChangeListener<T>> getListeners() {
        return listeners;
    }
}
